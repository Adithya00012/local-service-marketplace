import { useState, useEffect } from 'react';
import { getMyBookings } from '../api/bookings';

function MyBookingsPage() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        getMyBookings()
            .then(setBookings)
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

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
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default MyBookingsPage;