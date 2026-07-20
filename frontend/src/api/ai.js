import request from './client';

export function generateDescription(title, keywords) {
  return request('/services/generate-description', {
    method: 'POST',
    body: JSON.stringify({ title, keywords }),
  });
}

export function askChatbot(question) {
  return request(`/services/chat?question=${encodeURIComponent(question)}`);
}