import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { userApi } from '../api/userApi';
import type { UserResponse } from '../types';

export default function AdminUsersPage() {
  const { isAdmin, user: currentUser } = useAuth();
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [savingUserId, setSavingUserId] = useState<number | null>(null);
  const [error, setError] = useState('');

  const fetchUsers = () => {
    setLoading(true);
    setError('');
    userApi.getAll()
      .then((res) => {
        setUsers(res.data);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to load users.');
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (isAdmin) {
      fetchUsers();
    }
  }, [isAdmin]);

  const handleRoleChange = (userId: number, newRole: string) => {
    setSavingUserId(userId);
    setError('');
    userApi.updateRole(userId, { role: newRole })
      .then(() => {
        fetchUsers();
      })
      .catch((err) => {
        console.error('Failed to update user role:', err);
        const msg = err?.response?.data?.message ?? err?.response?.data;
        setError(typeof msg === 'string' ? msg : 'Failed to update user role.');
      })
      .finally(() => setSavingUserId(null));
  };

  if (!isAdmin) {
    return <div className="empty-text">Access Denied. Admin privileges required.</div>;
  }

  return (
    <div className="users-page">
      <h2 className="page-title">User Management (Admin)</h2>

      <section className="users-list-section">
        {loading ? (
          <p className="loading-text">Loading users...</p>
        ) : error ? (
          <p className="error-text">{error}</p>
        ) : users.length === 0 ? (
          <p className="empty-text">No users found.</p>
        ) : (
          <div className="users-table-container">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Username</th>
                  <th>Role</th>
                  <th className="actions-header">Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td>{u.username}</td>
                    <td>
                      <span className={`role-badge ${
                        u.role === 'ROLE_ADMIN' ? 'role-admin' :
                        u.role === 'ROLE_MENTOR' ? 'role-mentor' : 'role-junior'
                      }`}>
                        {u.role}
                      </span>
                    </td>
                    <td className="actions-cell">
                      {currentUser?.id === u.id ? (
                        <span className="self-action-text">(Self)</span>
                      ) : (
                        <select
                          className="input-field table-select"
                          value={u.role}
                          onChange={(e) => handleRoleChange(u.id, e.target.value)}
                          disabled={savingUserId === u.id}
                        >
                          <option value="ROLE_JUNIOR">ROLE_JUNIOR</option>
                          <option value="ROLE_MENTOR">ROLE_MENTOR</option>
                          <option value="ROLE_ADMIN">ROLE_ADMIN</option>
                        </select>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}
