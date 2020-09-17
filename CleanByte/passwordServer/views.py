from django.shortcuts import render
from django.http import HttpResponse

def password_server_home(request):
	return render(request, 'passwordServer/home.html')

def privacy(request):
	return render(request, 'passwordServer/privacyPolicy.html')

def terms(request):
	return render(request, 'passwordServer/termsConditions.html')
# Create your views here.
