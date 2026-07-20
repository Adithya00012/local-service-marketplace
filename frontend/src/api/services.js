import request from './client';

export function getAllServices({ page = 0, size = 10, title = '', category = '' } = {}) {
  const params = new URLSearchParams({ page, size });
  if (title) params.append('title', title);
  if (category) params.append('category', category);
  return request(`/services?${params.toString()}`);
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

export function semanticSearchServices(query) {
  return request(`/services/search/semantic?query=${encodeURIComponent(query)}`);
}