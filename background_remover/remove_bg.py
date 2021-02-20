import numpy as np
import imutils
import torch
import cv2
import os

from os.path import expanduser

from skimage import io, transform

from torch.utils.data import Dataset, DataLoader
from torch.autograd import Variable

from torchvision import transforms

from PIL import Image

from background_remover.data_loader import SalObjDataset
from background_remover.data_loader import RescaleT
from background_remover.data_loader import ToTensorLab

from background_remover.model import U2NET

import binascii
import collections
import datetime
import hashlib
import sys

import six
from six.moves.urllib.parse import quote

from google.oauth2 import service_account

HOME = '/home/cleanbyte/'

MODEL_NAME ='u2net'

MODEL_DIR = HOME + MODEL_NAME + '.pth'

OUTPUT_FORMAT ='.png'

def normalize_mask(d):
	ma = torch.max(d)
	mi = torch.min(d)

	dn = (d-mi)/(ma-mi)

	return dn

def save_output(image_name,pred,d_dir):
	predict = pred
	predict = predict.squeeze()
	predict_np = predict.cpu().data.numpy()

	im = Image.fromarray(predict_np*255).convert('RGB')

	img_name = image_name.split(os.sep)[-1]
	image = io.imread(image_name)
	imo = im.resize((image.shape[1],image.shape[0]),resample=Image.BILINEAR)

	pb_np = np.array(imo)

	aaa = img_name.split(".")
	bbb = aaa[0:-1]
	imidx = bbb[0]
	for i in range(1,len(bbb)):
		imidx = imidx + "." + bbb[i]

	filename = d_dir+imidx+'.png'
	imo.save(filename)

	return filename

def get_mask(net, image_name, data, h, w):
	inputs = data['image']
	
	inputs = inputs.type(torch.FloatTensor)
	
	inputs = Variable(inputs)

	d1,d2,d3,d4,d5,d6,d7 = net(inputs)

	mask = d1[:,0,:,:] 
	mask = normalize_mask(mask)

	mask = mask.squeeze()
	mask_np = mask.cpu().data.numpy()

	img_name = image_name.split(os.sep)[-1]
	image = io.imread(image_name)
	
	mask_np = Image.fromarray(mask_np*255).convert('RGB')

	mask_np = mask_np.resize((w,h),resample=Image.BILINEAR)

	del d1,d2,d3,d4,d5,d6,d7

	return mask_np.convert('L')

def test_fun(name, input_dir):
	return []

def remove(image_names, input_dir, output_dir, max_width=0, max_height=0):
	input_images = [(input_dir + im) for im in image_names]
	
	output_images = [(output_dir + ''.join(im.split('.')[:-1]) + OUTPUT_FORMAT) for im in image_names]
	output_image_size = [{'w':max_width,'h':max_height} for im in image_names]

	dataset = SalObjDataset(img_name_list=input_images,lbl_name_list=[],transform=transforms.Compose([RescaleT(320),ToTensorLab(flag=0)]))
	
	dataloader = DataLoader(dataset,batch_size=1,shuffle=False,num_workers=1)

	net = U2NET(3,1)
	
	net.load_state_dict(torch.load(MODEL_DIR, map_location='cpu'))

	net.eval()

	for i, data in enumerate(dataloader):
		img = cv2.imread(input_images[i])

		(h, w) = img.shape[:2]
		if max_width and w > max_width:
			img = imutils.resize(img, width=max_width)

		(h, w) = img.shape[:2]
		if max_height and h > max_height:
			img = imutils.resize(img, height=max_height)

		(h, w) = img.shape[:2]
		mask = get_mask(net, input_images[i], data, h, w)

		rgba = cv2.cvtColor(img, cv2.COLOR_RGB2RGBA)
		
		rgba[:, :, 3] = mask
		output_image_size[i]['w'] = w
		output_image_size[i]['h'] = h

		cv2.imwrite(output_images[i], rgba) 

	return output_images, output_image_size

def generate_signed_url(service_account_file, bucket_name, object_name, subresource=None, expiration=604800, http_method='GET', query_parameters=None, headers=None):
	if expiration > 604800:
		print('Expiration Time can\'t be longer than 604800 seconds (7 days).')
		sys.exit(1)

	escaped_object_name = quote(six.ensure_binary(object_name), safe=b'/~')
	canonical_uri = '/{}'.format(escaped_object_name)

	datetime_now = datetime.datetime.utcnow()
	request_timestamp = datetime_now.strftime('%Y%m%dT%H%M%SZ')
	datestamp = datetime_now.strftime('%Y%m%d')

	google_credentials = service_account.Credentials.from_service_account_file(service_account_file)

	client_email = google_credentials.service_account_email
	credential_scope = '{}/auto/storage/goog4_request'.format(datestamp)
	credential = '{}/{}'.format(client_email, credential_scope)

	if headers is None:
		headers = dict()

	host = '{}.storage.googleapis.com'.format(bucket_name)
	headers['host'] = host

	canonical_headers = ''
	ordered_headers = collections.OrderedDict(sorted(headers.items()))
	for k, v in ordered_headers.items():
		lower_k = str(k).lower()
		strip_v = str(v).lower()
		canonical_headers += '{}:{}\n'.format(lower_k, strip_v)

	signed_headers = ''
	for k, _ in ordered_headers.items():
		lower_k = str(k).lower()
		signed_headers += '{};'.format(lower_k)

	signed_headers = signed_headers[:-1]  # remove trailing ';'

	if query_parameters is None:
		query_parameters = dict()

	query_parameters['X-Goog-Algorithm'] = 'GOOG4-RSA-SHA256'
	query_parameters['X-Goog-Credential'] = credential
	query_parameters['X-Goog-Date'] = request_timestamp
	query_parameters['X-Goog-Expires'] = expiration
	query_parameters['X-Goog-SignedHeaders'] = signed_headers

	if subresource:
		query_parameters[subresource] = ''

	canonical_query_string = ''
	ordered_query_parameters = collections.OrderedDict(sorted(query_parameters.items()))

	for k, v in ordered_query_parameters.items():
		encoded_k = quote(str(k), safe='')
		encoded_v = quote(str(v), safe='')
		canonical_query_string += '{}={}&'.format(encoded_k, encoded_v)

	canonical_query_string = canonical_query_string[:-1]  # remove trailing '&'

	canonical_request = '\n'.join([http_method,canonical_uri,canonical_query_string,canonical_headers,signed_headers,'UNSIGNED-PAYLOAD'])

	canonical_request_hash = hashlib.sha256(canonical_request.encode()).hexdigest()

	string_to_sign = '\n'.join(['GOOG4-RSA-SHA256',request_timestamp,credential_scope,canonical_request_hash])

	# signer.sign() signs using RSA-SHA256 with PKCS1v15 padding
	signature = binascii.hexlify(google_credentials.signer.sign(string_to_sign)).decode()

	scheme_and_host = '{}://{}'.format('https', host)

	signed_url = '{}{}?{}&x-goog-signature={}'.format(scheme_and_host, canonical_uri, canonical_query_string, signature)

	return signed_url

#remove_bg(['IMG_0309.JPG'], '/home/cleanbyte/', '/home/cleanbyte/output/')