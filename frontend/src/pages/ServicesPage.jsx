import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllServices, createService, deleteService } from '../api/services';
import { createBooking } from '../api/bookings';
import { useAuth } from '../context/AuthContext';
import { generateDescription } from '../api/ai';
import { getReviewSummary } from '../api/reviews';
import { semanticSearchServices } from '../api/services';

function ServicesPage() {
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);

    const [searchTitle, setSearchTitle] = useState('');
    const [searchCategory, setSearchCategory] = useState('');
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [price, setPrice] = useState('');
    const [category, setCategory] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const [keywords, setKeywords] = useState('');
    const [generating, setGenerating] = useState(false);

    const [expandedServiceId, setExpandedServiceId] = useState(null);
    const [summary, setSummary] = useState('');
    const [loadingSummary, setLoadingSummary] = useState(false);

    const [aiQuery, setAiQuery] = useState('');
    const [aiSearching, setAiSearching] = useState(false);

    const { user } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        loadServices();
    }, [page]);

    async function loadServices() {
        setLoading(true);
        try {
            const data = await getAllServices({
                page,
                size: 6,
                title: searchTitle,
                category: searchCategory,
            });
            setServices(data.content);
            setTotalPages(data.totalPages);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    function handleSearch(e) {
        e.preventDefault();
        setPage(0);
        loadServices();
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

    async function handleGenerateDescription() {
        if (!title) {
            setError('Enter a title first');
            return;
        }
        setGenerating(true);
        try {
            const result = await generateDescription(title, keywords);
            setDescription(result.description);
        } catch (err) {
            setError(err.message);
        } finally {
            setGenerating(false);
        }
    }

    async function handleToggleReviews(serviceId) {
        if (expandedServiceId === serviceId) {
            setExpandedServiceId(null);
            return;
        }
        setExpandedServiceId(serviceId);
        setLoadingSummary(true);
        setSummary('');
        try {
            const result = await getReviewSummary(serviceId);
            setSummary(result.summary);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoadingSummary(false);
        }
    }

    async function handleAiSearch(e) {
        e.preventDefault();
        if (!aiQuery.trim()) return;
        setAiSearching(true);
        setError('');
        try {
            const results = await semanticSearchServices(aiQuery);
            setServices(results);
            setTotalPages(0); // hide normal pagination for AI search results
        } catch (err) {
            setError(err.message);
        } finally {
            setAiSearching(false);
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

            <form onSubmit={handleSearch} className="flex gap-2 mb-6">
                <input
                    type="text"
                    placeholder="Search by title..."
                    value={searchTitle}
                    onChange={(e) => setSearchTitle(e.target.value)}
                    className="px-3 py-2 border border-gray-300 rounded-md flex-1"
                />
                <input
                    type="text"
                    placeholder="Category..."
                    value={searchCategory}
                    onChange={(e) => setSearchCategory(e.target.value)}
                    className="px-3 py-2 border border-gray-300 rounded-md flex-1"
                />
                <button
                    type="submit"
                    className="bg-gray-800 text-white px-4 py-2 rounded-md hover:bg-gray-900"
                >
                    Search
                </button>
            </form>
            <form onSubmit={handleAiSearch} className="flex gap-2 mb-6">
                <input
                    type="text"
                    placeholder="✨ Describe what you need (e.g. 'my tap is leaking')..."
                    value={aiQuery}
                    onChange={(e) => setAiQuery(e.target.value)}
                    className="px-3 py-2 border border-purple-300 rounded-md flex-1"
                />
                <button
                    type="submit"
                    disabled={aiSearching}
                    className="bg-purple-600 text-white px-4 py-2 rounded-md hover:bg-purple-700 disabled:opacity-50"
                >
                    {aiSearching ? 'Searching...' : 'AI Search'}
                </button>
            </form>

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
                    <div className="flex gap-2">
                        <input
                            type="text"
                            placeholder="Keywords for AI (e.g. 5 years experience, eco-friendly)"
                            value={keywords}
                            onChange={(e) => setKeywords(e.target.value)}
                            className="flex-1 px-3 py-2 border border-gray-300 rounded-md text-sm"
                        />
                        <button
                            type="button"
                            onClick={handleGenerateDescription}
                            disabled={generating}
                            className="bg-purple-600 text-white px-3 py-2 rounded-md text-sm hover:bg-purple-700 disabled:opacity-50"
                        >
                            {generating ? 'Generating...' : '✨ AI Generate'}
                        </button>
                    </div>
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
                            <button
                                onClick={() => handleToggleReviews(s.id)}
                                className="text-purple-600 text-xs hover:underline mt-1"
                            >
                                {expandedServiceId === s.id ? 'Hide Reviews' : '✨ AI Review Summary'}
                            </button>

                            {expandedServiceId === s.id && (
                                <div className="mt-2 p-2 bg-purple-50 rounded text-xs text-gray-700">
                                    {loadingSummary ? 'Summarizing reviews...' : summary}
                                </div>
                            )}

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

            {totalPages > 1 && (
                <div className="flex justify-center items-center gap-4 mt-6">
                    <button
                        onClick={() => setPage(Math.max(0, page - 1))}
                        disabled={page === 0}
                        className="px-3 py-1 bg-white border border-gray-300 rounded disabled:opacity-50"
                    >
                        Previous
                    </button>
                    <span className="text-sm text-gray-600">
                        Page {page + 1} of {totalPages}
                    </span>
                    <button
                        onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                        disabled={page >= totalPages - 1}
                        className="px-3 py-1 bg-white border border-gray-300 rounded disabled:opacity-50"
                    >
                        Next
                    </button>
                </div>
            )}
        </div>
    );
}

export default ServicesPage;