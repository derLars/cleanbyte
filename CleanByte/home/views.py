from django.shortcuts import render
from django.http import HttpResponse

def home(request):
	return render(request, 'home/home.html')

def legal(request):
	return render(request, 'home/legal_fr.html')

def legal_de(request):
	return render(request, 'home/legal_de.html')

def legal_fr(request):
	return render(request, 'home/legal_fr.html')

def license(request):
	return render(request, 'home/license.html')

