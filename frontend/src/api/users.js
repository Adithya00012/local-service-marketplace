import request from './client';

export function getAllUsers() {
  return request('/users');
}

export function getUserById(id) {
  return request(`/users/${id}`);
}

export function deleteUser(id) {
  return request(`/users/${id}`, { method: 'DELETE' });
}