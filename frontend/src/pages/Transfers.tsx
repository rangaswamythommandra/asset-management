import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { PlusIcon, PencilIcon, TrashIcon, CheckIcon, XMarkIcon } from '@heroicons/react/24/outline';
import type { Transfer, Asset, Base, User, FilterOptions } from '../types';
import apiService from '../services/api';
import toast from 'react-hot-toast';
import { useAuth } from '../contexts/AuthContext';

const schema = yup.object({
  assetId: yup.number().required('Asset is required'),
  fromBaseId: yup.number().required('From base is required'),
  toBaseId: yup.number().required('To base is required'),
  transferDate: yup.string().required('Transfer date is required'),
  reason: yup.string().required('Reason is required'),
}).required();

type TransferFormData = yup.InferType<typeof schema>;

const Transfers: React.FC = () => {
  const [transfers, setTransfers] = useState<Transfer[]>([]);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [bases, setBases] = useState<Base[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingTransfer, setEditingTransfer] = useState<Transfer | null>(null);
  const [filters, setFilters] = useState<FilterOptions>({});
  const { user } = useAuth();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TransferFormData>({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    loadData();
  }, [filters]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [transfersResponse, assetsResponse, basesResponse] = await Promise.all([
        apiService.getTransfers(filters),
        apiService.getAssets(),
        apiService.getBases()
      ]);
      setTransfers(transfersResponse.data);
      setAssets(assetsResponse.data);
      setBases(basesResponse.data);
    } catch (error) {
      toast.error('Failed to load transfers data');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: TransferFormData) => {
    try {
      const transferData = {
        assetId: data.assetId,
        fromBaseId: data.fromBaseId,
        toBaseId: data.toBaseId,
        transferDate: data.transferDate,
        reason: data.reason,
      };
      if (editingTransfer) {
        await apiService.updateTransfer(editingTransfer.id, transferData);
        toast.success('Transfer updated successfully');
      } else {
        await apiService.createTransfer(transferData);
        toast.success('Transfer created successfully');
      }
      reset();
      setShowForm(false);
      setEditingTransfer(null);
      loadData();
    } catch (error: any) {
      toast.error(error.response?.data || 'Failed to save transfer');
    }
  };

  const handleEdit = (transfer: Transfer) => {
    setEditingTransfer(transfer);
    reset({
      assetId: transfer.asset.id,
      fromBaseId: transfer.fromBase.id,
      toBaseId: transfer.toBase.id,
      transferDate: transfer.transferDate.split('T')[0],
      reason: transfer.reason,
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this transfer?')) {
      try {
        await apiService.deleteTransfer(id);
        toast.success('Transfer deleted successfully');
        loadData();
      } catch (error) {
        toast.error('Failed to delete transfer');
      }
    }
  };

  const handleApprove = async (id: number) => {
    try {
      await apiService.approveTransfer(id);
      toast.success('Transfer approved');
      loadData();
    } catch (error) {
      toast.error('Failed to approve transfer');
    }
  };

  const handleReject = async (id: number) => {
    const reason = prompt('Reason for rejection?');
    if (!reason) return;
    try {
      await apiService.rejectTransfer(id, reason);
      toast.success('Transfer rejected');
      loadData();
    } catch (error) {
      toast.error('Failed to reject transfer');
    }
  };

  const handleCancel = () => {
    reset();
    setShowForm(false);
    setEditingTransfer(null);
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
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-military-900">Transfers</h1>
          <p className="text-military-600">Manage asset transfers between bases</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="btn-primary flex items-center space-x-2"
        >
          <PlusIcon className="h-5 w-5" />
          <span>Create Transfer</span>
        </button>
      </div>

      {/* Filters */}
      <div className="card">
        <h3 className="text-lg font-medium text-military-900 mb-4">Filters</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">Date From</label>
            <input type="date" className="input-field" value={filters.dateFrom || ''} onChange={e => setFilters({ ...filters, dateFrom: e.target.value })} />
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">Date To</label>
            <input type="date" className="input-field" value={filters.dateTo || ''} onChange={e => setFilters({ ...filters, dateTo: e.target.value })} />
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">From Base</label>
            <select className="input-field" value={filters.baseId || ''} onChange={e => setFilters({ ...filters, baseId: e.target.value ? Number(e.target.value) : undefined })}>
              <option value="">All Bases</option>
              {bases.map(base => <option key={base.id} value={base.id}>{base.name}</option>)}
            </select>
          </div>
        </div>
      </div>

      {/* Transfer Form */}
      {showForm && (
        <div className="card">
          <h3 className="text-lg font-medium text-military-900 mb-4">{editingTransfer ? 'Edit Transfer' : 'Create New Transfer'}</h3>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">Asset *</label>
                <select {...register('assetId')} className="input-field">
                  <option value="">Select Asset</option>
                  {assets.map(asset => <option key={asset.id} value={asset.id}>{asset.assetType.name} ({asset.serialNumber})</option>)}
                </select>
                {errors.assetId && <p className="mt-1 text-sm text-red-600">{errors.assetId.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">From Base *</label>
                <select {...register('fromBaseId')} className="input-field">
                  <option value="">Select Base</option>
                  {bases.map(base => <option key={base.id} value={base.id}>{base.name}</option>)}
                </select>
                {errors.fromBaseId && <p className="mt-1 text-sm text-red-600">{errors.fromBaseId.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">To Base *</label>
                <select {...register('toBaseId')} className="input-field">
                  <option value="">Select Base</option>
                  {bases.map(base => <option key={base.id} value={base.id}>{base.name}</option>)}
                </select>
                {errors.toBaseId && <p className="mt-1 text-sm text-red-600">{errors.toBaseId.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">Transfer Date *</label>
                <input {...register('transferDate')} type="date" className="input-field" />
                {errors.transferDate && <p className="mt-1 text-sm text-red-600">{errors.transferDate.message}</p>}
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">Reason *</label>
              <textarea {...register('reason')} rows={3} className="input-field" />
              {errors.reason && <p className="mt-1 text-sm text-red-600">{errors.reason.message}</p>}
            </div>
            <div className="flex justify-end space-x-3">
              <button type="button" onClick={handleCancel} className="btn-secondary">Cancel</button>
              <button type="submit" className="btn-primary">{editingTransfer ? 'Update Transfer' : 'Create Transfer'}</button>
            </div>
          </form>
        </div>
      )}

      {/* Transfers Table */}
      <div className="card">
        <h3 className="text-lg font-medium text-military-900 mb-4">Transfer History</h3>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-military-200">
            <thead className="bg-military-50">
              <tr>
                <th className="table-header">Date</th>
                <th className="table-header">Asset</th>
                <th className="table-header">From</th>
                <th className="table-header">To</th>
                <th className="table-header">Status</th>
                <th className="table-header">Reason</th>
                <th className="table-header">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-military-200">
              {transfers.map(transfer => (
                <tr key={transfer.id} className="hover:bg-military-50">
                  <td className="table-cell">{new Date(transfer.transferDate).toLocaleDateString()}</td>
                  <td className="table-cell">{transfer.asset.assetType.name} ({transfer.asset.serialNumber})</td>
                  <td className="table-cell">{transfer.fromBase.name}</td>
                  <td className="table-cell">{transfer.toBase.name}</td>
                  <td className="table-cell">{transfer.status}</td>
                  <td className="table-cell">{transfer.reason}</td>
                  <td className="table-cell">
                    <div className="flex space-x-2">
                      {(user?.role === 'ADMIN' || user?.role === 'BASE_COMMANDER') && transfer.status === 'PENDING' && (
                        <>
                          <button onClick={() => handleApprove(transfer.id)} className="text-green-600 hover:text-green-900"><CheckIcon className="h-4 w-4" /></button>
                          <button onClick={() => handleReject(transfer.id)} className="text-red-600 hover:text-red-900"><XMarkIcon className="h-4 w-4" /></button>
                        </>
                      )}
                      <button onClick={() => handleEdit(transfer)} className="text-primary-600 hover:text-primary-900"><PencilIcon className="h-4 w-4" /></button>
                      <button onClick={() => handleDelete(transfer.id)} className="text-red-600 hover:text-red-900"><TrashIcon className="h-4 w-4" /></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Transfers; 