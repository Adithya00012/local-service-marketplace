import { useState, useEffect } from 'react';
import { getMyBookings } from '../api/bookings';
import { createReview } from '../api/reviews';

function MyBookingsPage() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [reviewingId, setReviewingId] = useState(null);
    const [rating, setRating] = useState(5);
    const [comment, setComment] = useState('');
    const [reviewedIds, setReviewedIds] = useState([]);

    useEffect(() => {
        loadBookings();
    }, []);

    function loadBookings() {
        setLoading(true);
        getMyBookings()
            .then(setBookings)
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }

    async function handleSubmitReview(bookingId) {
        try {
            await createReview(bookingId, rating, comment);
            setReviewingId(null);
            setRating(5);
            setComment('');
            loadBookings();
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <h1 className="text-xl font-bold text-gray-800 mb-6">My Bookings</h1>

            {error && <p className="text-red-600 mb-4">{error}</p>}

            {loading ? (
                <p className="text-gray-500">Loading...</p>
            ) : bookings.length === 0 ? (
                <p className="text-gray-500">No bookings yet.</p>
            ) : (
                <div className="space-y-3">
                    {bookings.map((b) => (
                        <div key={b.id} className="bg-white p-4 rounded-lg shadow-sm">
                            <h3 className="font-semibold text-gray-800">{b.serviceTitle}</h3>
                            <p className="text-sm text-gray-500">Provider: {b.providerName}</p>
                            <p className="text-sm text-gray-500">
                                Date: {new Date(b.bookingDate).toLocaleString()}
                            </p>
                            <span
                                className={`inline-block mt-2 px-2 py-1 rounded text-xs font-medium ${b.status === 'CONFIRMED'
                                        ? 'bg-green-100 text-green-700'
                                        : b.status === 'CANCELLED'
                                            ? 'bg-red-100 text-red-700'
                                            : b.status === 'COMPLETED'
                                                ? 'bg-blue-100 text-blue-700'
                                                : 'bg-yellow-100 text-yellow-700'
                                    }`}
                            >
                                {b.status}
                            </span>

                            {b.status === 'COMPLETED' && !b.isReviewed && ( 
                                <div className="mt-3">
                                    {reviewingId === b.id ? (
                                        <div className="space-y-2">
                                            <select
                                                value={rating}
                                                onChange={(e) => setRating(Number(e.target.value))}
                                                className="border border-gray-300 rounded px-2 py-1 text-sm"
                                            >
                                                {[5, 4, 3, 2, 1].map((n) => (
                                                    <option key={n} value={n}>
                                                        {n} Star{n > 1 ? 's' : ''}
                                                    </option>
                                                ))}
                                            </select>
                                            <textarea
                                                placeholder="Leave a comment..."
                                                value={comment}
                                                onChange={(e) => setComment(e.target.value)}
                                                className="w-full border border-gray-300 rounded px-2 py-1 text-sm"
                                            />
                                            <div className="space-x-2">
                                                <button
                                                    onClick={() => handleSubmitReview(b.id)}
                                                    className="bg-blue-600 text-white px-3 py-1 rounded text-xs hover:bg-blue-700"
                                                >
                                                    Submit Review
                                                </button>
                                                <button
                                                    onClick={() => setReviewingId(null)}
                                                    className="text-gray-500 text-xs hover:underline"
                                                >
                                                    Cancel
                                                </button>
                                            </div>
                                        </div>
                                    ) : (
                                        <button
                                            onClick={() => setReviewingId(b.id)}
                                            className="text-blue-600 text-xs hover:underline"
                                        >
                                            Leave a Review
                                        </button>
                                    )}
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default MyBookingsPage;