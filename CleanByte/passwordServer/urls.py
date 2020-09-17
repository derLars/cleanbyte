from django.urls import path
from django.views.generic import RedirectView
from . import views

urlpatterns = [
    path('/', views.password_server_home, name='password_server_home'),
    path('/privacy', views.privacy, name='privacy'), 
    path('/terms', views.terms, name='terms'),     
]