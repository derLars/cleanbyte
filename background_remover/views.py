from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.core.files.storage import FileSystemStorage
from django.conf import settings

from background_remover.remove_bg import *

import logging
import traceback

import pyrebase

config = {
	"apiKey": "AIzaSyDgU4RbdwaQc-vMmU6MTDtDtYcoBiRtVTM",
    "authDomain": "cleanbyte-backgroundremover.firebaseapp.com",
    "projectId": "cleanbyte-backgroundremover",
    "databaseURL": "https://databaseName.firebaseio.com",
    "storageBucket": "cleanbyte-backgroundremover.appspot.com",
    "messagingSenderId": "695909037490",
    "appId": "1:695909037490:web:4a907c4e656ed7e7bb20ab",
    "measurementId": "G-1KP5KFNC62"
}


HOME = '/home/cleanbyte/'
STORAGE_CREDENTIALS = HOME + 'cleanbyte-backgroundremover-firebase-adminsdk-dem89-229c68dd8b.json'
STORAGE_BUCKET = 'cleanbyte-backgroundremover.appspot.com'

logger = logging.getLogger(__name__)

def index(request):
	logger = logging.LoggerAdapter(logging.getLogger(__name__), {"ip" : request.META.get("REMOTE_ADDR")})
	logger.info('/background_remover called')		
	
	return render(request, 'background_remover/index.html')

def remove_bg(request):
	logger = logging.LoggerAdapter(logging.getLogger(__name__), {"ip" : request.META.get("REMOTE_ADDR")})

	logger.info('Removing background: started')
	
	data = {}
	try:
		uploaded_file = request.FILES['image']


		firebase = pyrebase.initialize_app(config)
		storage = firebase.storage()

		fs = FileSystemStorage()
		name = fs.save(uploaded_file.name, uploaded_file)
		
		url = fs.url(name)
		
		storage.child('original_' + name).put(settings.MEDIA_ROOT + "/" + name)

		#processed_files = remove_bg([name], settings.MEDIA_ROOT + "/", settings.MEDIA_ROOT + "/", max_width=1024,max_height=768)
		#processed_files = []
		processed_files, processed_file_sizes = remove([name], settings.MEDIA_ROOT + "/", settings.MEDIA_ROOT + "/", max_width=1024,max_height=768)

		download_url = None
		filename = None
		for i in range(len(processed_files)):
			filename = processed_files[i].split(os.sep)[-1]
			
			storage.child(filename).put(processed_files[i])	

			download_url = generate_signed_url(STORAGE_CREDENTIALS, STORAGE_BUCKET, filename, subresource=None, expiration=600, http_method='GET', query_parameters=None, headers=None)

			fs.delete(name)
			
			fs.delete(filename)

			data['name'] = {'download_url': download_url,'width':processed_file_sizes[i]['w'], 'height':processed_file_sizes[i]['h']}

		logger.info('Removing background: done')
	except Exception as ex:
		logger.error('Exception: ' + str(ex))
		logger.error('Exception: ' + traceback.format_exc())

	return JsonResponse(data)