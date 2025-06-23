import React, { useState, useEffect } from 'react';
import type { AuditLog, User, Base, AssetType, FilterOptions } from '../types';
import apiService from '../services/api';
import toast from 'react-hot-toast';

const AuditLogs: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [bases, setBases] = useState<Base[]>([]);
  const [assetTypes, setAssetTypes] = useState<AssetType[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<FilterOptions>({});

  useEffect(() => {
    loadData();
  }, [filters]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [logsResponse, usersResponse, basesResponse, assetTypesResponse] = await Promise.all([
        apiService.getAuditLogs(filters),
        apiService.getUsers(),
        apiService.getBases(),
        apiService.getAssetTypes()
      ]);
      setLogs(logsResponse.data);
      setUsers(usersResponse.data);
      setBases(basesResponse.data);
      setAssetTypes(assetTypesResponse.data);
    } catch (error) {
      toast.error('Failed to load audit logs');
    } finally {
      setLoading(false);
    }
  };

  const clearFilters = () => {
    setFilters({});
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-military-900">Audit Logs</h1>
        <p className="text-military-600">System activity and transaction logs</p>
      </div>
      <div className="card">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-medium text-military-900">Filters</h3>
          <button 
            onClick={clearFilters}
            className="px-3 py-1 text-sm bg-military-100 text-military-700 rounded hover:bg-military-200"
          >
            Clear Filters
          </button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">Date From</label>
            <input 
              type="date" 
              className="input-field" 
              value={filters.dateFrom || ''} 
              onChange={e => setFilters({ ...filters, dateFrom: e.target.value })} 
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">Date To</label>
            <input 
              type="date" 
              className="input-field" 
              value={filters.dateTo || ''} 
              onChange={e => setFilters({ ...filters, dateTo: e.target.value })} 
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">Base</label>
            <select 
              className="input-field" 
              value={filters.baseId || ''} 
              onChange={e => setFilters({ ...filters, baseId: e.target.value ? Number(e.target.value) : undefined })}
            >
              <option value="">All Bases</option>
              {bases.map(base => <option key={base.id} value={base.id}>{base.name}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">Asset Type</label>
            <select 
              className="input-field" 
              value={filters.assetTypeId || ''} 
              onChange={e => setFilters({ ...filters, assetTypeId: e.target.value ? Number(e.target.value) : undefined })}
            >
              <option value="">All Asset Types</option>
              {assetTypes.map(type => <option key={type.id} value={type.id}>{type.name}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">User</label>
            <select 
              className="input-field" 
              value={filters.userId || ''} 
              onChange={e => setFilters({ ...filters, userId: e.target.value ? Number(e.target.value) : undefined })}
            >
              <option value="">All Users</option>
              {users.map(user => <option key={user.id} value={user.id}>{user.username}</option>)}
            </select>
          </div>
        </div>
      </div>
      <div className="card">
        <h3 className="text-lg font-medium text-military-900 mb-4">Audit Log Entries ({logs.length})</h3>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-military-200">
            <thead className="bg-military-50">
              <tr>
                <th className="table-header">Timestamp</th>
                <th className="table-header">User</th>
                <th className="table-header">Action</th>
                <th className="table-header">Entity</th>
                <th className="table-header">Details</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-military-200">
              {logs.map(log => (
                <tr key={log.id} className="hover:bg-military-50">
                  <td className="table-cell">{new Date(log.timestamp).toLocaleString()}</td>
                  <td className="table-cell">{log.user.username}</td>
                  <td className="table-cell">
                    <span className={`px-2 py-1 text-xs rounded-full ${
                      log.action === 'CREATE' ? 'bg-green-100 text-green-800' :
                      log.action === 'UPDATE' ? 'bg-blue-100 text-blue-800' :
                      log.action === 'DELETE' ? 'bg-red-100 text-red-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {log.action}
                    </span>
                  </td>
                  <td className="table-cell">{log.entity} #{log.entityId}</td>
                  <td className="table-cell">{log.details}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {logs.length === 0 && (
            <div className="text-center py-8 text-military-500">
              No audit logs found matching the current filters.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AuditLogs; 