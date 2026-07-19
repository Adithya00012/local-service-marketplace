import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllServices, createService, deleteService } from '../api/services';
import { createBooking } from '../api/bookings';
import { useAuth } from '../context/AuthContext';

function ServicesPage() {
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [price, setPrice] = useState('');
    const [category, setCategory] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const { user } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        loadServices();
    }, []);

    async function loadServices() {
        setLoading(true);
        try {
            const data = await getAllServices();
            setServices(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function handleCreate(e) {
        e.preventDefault();
        setSubmitting(true);
        setError('');
        try {
            await createService(title, description, Number(price), category);
            setTitle('');
            setDescription('');
            setPrice('');
            setCategory('');
            setShowForm(false);
            loadServices();
        } catch (err) {
            setError(err.message);
        } finally {
            setSubmitting(false);
        }
    }

    async function handleDelete(id) {
        if (!window.confirm('Delete this service?')) return;
        try {
            await deleteService(id);
            setServices(services.filter((s) => s.id !== id));
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleBook(serviceId) {
        const date = prompt(
            'Enter booking date/time (YYYY-MM-DDTHH:mm), e.g. 2026-08-01T14:00'
        );
        if (!date) return;

        try {
            await createBooking(serviceId, date);
            alert('Booking created!');
            navigate('/my-bookings');
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-xl font-bold text-gray-800">Browse Services</h1>
                {user && user.role === 'PROVIDER' && (
                    <button
                        onClick={() => setShowForm(!showForm)}
                        className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
                    >
                        {showForm ? 'Cancel' : '+ Post a Service'}
                    </button>
                )}
            </div>

            {error && <p className="text-red-600 mb-4">{error}</p>}

            {showForm && (
                <form
                    onSubmit={handleCreate}
                    className="bg-white p-6 rounded-lg shadow-sm mb-6 space-y-3"
                >
                    <input
                        type="text"
                        placeholder="Title (e.g. House Cleaning)"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                        className="w-full px-3 py-2 border border-gray-300 rounded-md"
                    />
                    <textarea
                        placeholder="Description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md"
                    />
                    <input
                        type="number"
                        placeholder="Price"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        required
                        min="0"
                        step="0.01"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md"
                    />
                    <input
                        type="text"
                        placeholder="Category (e.g. Cleaning)"
                        value={category}
                        onChange={(e) => setCategory(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md"
                    />
                    <button
                        type="submit"
                        disabled={submitting}
                        className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 disabled:opacity-50"
                    >
                        {submitting ? 'Posting...' : 'Post Service'}
                    </button>
                </form>
            )}

            {loading ? (
                <p className="text-gray-500">Loading services...</p>
            ) : services.length === 0 ? (
                <p className="text-gray-500">No services posted yet.</p>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {services.map((s) => (
                        <div key={s.id} className="bg-white p-4 rounded-lg shadow-sm">
                            <h3 className="font-semibold text-gray-800">{s.title}</h3>
                            <p className="text-sm text-gray-500 mb-2">{s.category}</p>
                            <p className="text-gray-600 text-sm mb-2">{s.description}</p>
                            <p className="text-blue-600 font-bold">₹{s.price}</p>
                            <p className="text-xs text-gray-400 mt-2">by {s.providerName}</p>

                            <div className="mt-2 space-x-3">
                                {user && user.role === 'CUSTOMER' && (
                                    <button
                                        onClick={() => handleBook(s.id)}
                                        className="text-green-600 text-xs hover:underline"
                                    >
                                        Book Now
                                    </button>
                                )}

                                {user?.name === s.providerName && (
                                    <button
                                        onClick={() => handleDelete(s.id)}
                                        className="text-red-600 text-xs hover:underline"
                                    >
                                        Delete
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default ServicesPage;