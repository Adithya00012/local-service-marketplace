import { useState, useEffect } from 'react';
import { getReceivedBookings, updateBookingStatus } from '../api/bookings';

function ReceivedBookingsPage() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadBookings();
    }, []);

    function loadBookings() {
        setLoading(true);
        getReceivedBookings()
            .then(setBookings)
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }

    async function handleStatusChange(id, status) {
        try {
            await updateBookingStatus(id, status);
            loadBookings();
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <h1 className="text-xl font-bold text-gray-800 mb-6">Received Bookings</h1>

            {error && <p className="text-red-600 mb-4">{error}</p>}

            {loading ? (
                <p className="text-gray-500">Loading...</p>
            ) : bookings.length === 0 ? (
                <p className="text-gray-500">No bookings received yet.</p>
            ) : (
                <div className="space-y-3">
                    {bookings.map((b) => (
                        <div key={b.id} className="bg-white p-4 rounded-lg shadow-sm">
                            <h3 className="font-semibold text-gray-800">{b.serviceTitle}</h3>
                            <p className="text-sm text-gray-500">Customer: {b.customerName}</p>
                            <p className="text-sm text-gray-500">
                                Date: {new Date(b.bookingDate).toLocaleString()}
                            </p>
                            <p className="text-sm font-medium mt-1">Status: {b.status}</p>

                            {b.status === 'PENDING' && (
                                <div className="mt-2 space-x-2">
                                    <button
                                        onClick={() => handleStatusChange(b.id, 'CONFIRMED')}
                                        className="bg-green-600 text-white px-3 py-1 rounded text-xs hover:bg-green-700"
                                    >
                                        Confirm
                                    </button>
                                    <button
                                        onClick={() => handleStatusChange(b.id, 'CANCELLED')}
                                        className="bg-red-600 text-white px-3 py-1 rounded text-xs hover:bg-red-700"
                                    >
                                        Cancel
                                    </button>
                                </div>
                            )}
                            {b.status === 'CONFIRMED' && (
                                <button
                                    onClick={() => handleStatusChange(b.id, 'COMPLETED')}
                                    className="mt-2 bg-blue-600 text-white px-3 py-1 rounded text-xs hover:bg-blue-700"
                                >
                                    Mark Completed
                                </button>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default ReceivedBookingsPage;