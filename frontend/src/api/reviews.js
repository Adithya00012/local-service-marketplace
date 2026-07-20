import request from './client';

export function getReviewsForService(serviceId) {
  return request(`/reviews/service/${serviceId}`);
}

export function createReview(bookingId, rating, comment) {
  return request('/reviews', {
    method: 'POST',
    body: JSON.stringify({ bookingId, rating, comment }),
  });
}

export function getReviewSummary(serviceId) {
  return request(`/reviews/service/${serviceId}/summary`);
}