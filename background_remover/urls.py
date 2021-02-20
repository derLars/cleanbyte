from django.urls import path
from django.conf import settings
from django.views.generic import RedirectView
from django.conf.urls.static import static

from . import views

urlpatterns = [
    path('', views.index, name='index'), 
    path('/remove_bg/', views.remove_bg, name='remove_bg')  
]+ static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)