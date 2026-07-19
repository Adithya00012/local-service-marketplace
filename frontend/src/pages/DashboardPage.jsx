import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllUsers, deleteUser } from '../api/users';
import { useAuth } from '../context/AuthContext';

function DashboardPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const { user, logoutUser } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadUsers();
  }, []);

  async function loadUsers() {
    setLoading(true);
    setError('');
    try {
      const data = await getAllUsers();
      setUsers(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id) {
    if (!window.confirm('Are you sure you want to delete this user?')) return;

    try {
      await deleteUser(id);
      setUsers(users.filter((u) => u.id !== id));
    } catch (err) {
      setError(err.message);
    }
  }

  function handleLogout() {
    logoutUser();
    navigate('/login');
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm px-6 py-4 flex justify-between items-center">
        <div>
          <h1 className="text-xl font-bold text-gray-800">
            Local Service Marketplace
          </h1>
          <p className="text-sm text-gray-500">Welcome, {user?.name}</p>
        </div>
        <div className="flex items-center">
          <a href="/services" className="text-blue-600 hover:underline mr-4">
            Browse Services
          </a>
          <a href="/my-bookings" className="text-blue-600 hover:underline mr-4">
            My Bookings
          </a>

          <a href="/received-bookings"
          className="text-blue-600 hover:underline mr-4"
          >
          Received Bookings
        </a>
        <button
          onClick={handleLogout}
          className="bg-gray-200 text-gray-800 px-4 py-2 rounded-md hover:bg-gray-300"
        >
          Log Out
        </button>
    </div>
      </header >

    <main className="p-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">
        All Users
      </h2>

      {error && <p className="text-red-600 mb-4">{error}</p>}

      {loading ? (
        <p className="text-gray-500">Loading users...</p>
      ) : (
        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-gray-100 text-gray-600 text-sm">
              <tr>
                <th className="px-4 py-3">Name</th>
                <th className="px-4 py-3">Email</th>
                <th className="px-4 py-3">Role</th>
                <th className="px-4 py-3"></th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-t border-gray-100">
                  <td className="px-4 py-3">{u.name}</td>
                  <td className="px-4 py-3">{u.email}</td>
                  <td className="px-4 py-3">{u.role}</td>
                  <td className="px-4 py-3 text-right">
                    <button
                      onClick={() => handleDelete(u.id)}
                      className="text-red-600 hover:underline text-sm"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
    </div >
  );
}

export default DashboardPage;