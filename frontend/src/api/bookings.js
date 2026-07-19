import request from './client';

export function createBooking(serviceId, bookingDate) {
  return request('/bookings', {
    method: 'POST',
    body: JSON.stringify({ serviceId, bookingDate }),
  });
}

export function getMyBookings() {
  return request('/bookings/my-bookings');
}

export function getReceivedBookings() {
  return request('/bookings/received');
}

export function updateBookingStatus(id, status) {
  return request(`/bookings/${id}/status?status=${status}`, {
    method: 'PUT',
  });
}