from django.urls import path
from django.views.generic import RedirectView
from . import views

urlpatterns = [
    path('', views.home, name='home'), 
    path('legal/', RedirectView.as_view(pattern_name='legal_fr', permanent=False)),
    path('legal/de', views.legal_de, name='legal_de'), 
    path('legal/fr', views.legal_fr, name='legal_fr'),    
    path('copyrights', views.license, name='copyrights'),  
]