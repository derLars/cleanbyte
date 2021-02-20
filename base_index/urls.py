from django.urls import path
from django.views.generic import RedirectView
from . import views

urlpatterns = [
    path('', views.index, name='index'),  
    path('terms', views.terms, name='terms'), 
    path('copyrights', views.copyrights, name='copyrights'), 
    path('about', views.about, name='about'), 
]