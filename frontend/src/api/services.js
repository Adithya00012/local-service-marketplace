import request from './client';

export function getAllServices() {
  return request('/services');
}

export function createService(title, description, price, category) {
  return request('/services', {
    method: 'POST',
    body: JSON.stringify({ title, description, price, category }),
  });
}

export function deleteService(id) {
  return request(`/services/${id}`, { method: 'DELETE' });
}