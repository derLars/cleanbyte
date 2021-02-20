from django.shortcuts import render
from django.http import HttpResponse

def index(request):
	return render(request, 'base_index/index.html')

def terms(request):
	return render(request, 'base_index/terms.html')

def copyrights(request):
	return render(request, 'base_index/copyrights.html')

def about(request):
	return render(request, 'base_index/about.html')
