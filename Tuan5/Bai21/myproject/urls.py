"""
URL configuration for myproject project.
"""
from django.contrib import admin
from django.urls import path
from django.http import JsonResponse


def health_check(request):
    return JsonResponse({'status': 'OK', 'service': 'Django'})


def home(request):
    return JsonResponse({
        'message': 'Django + Celery + Redis',
        'endpoints': ['/admin/', '/health/', '/celery-test/']
    })


urlpatterns = [
    path('admin/', admin.site.urls),
    path('', home),
    path('health/', health_check),
]
