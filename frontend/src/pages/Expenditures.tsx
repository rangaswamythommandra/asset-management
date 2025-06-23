import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import type { Expenditure, Asset, Base, User, FilterOptions } from '../types';
import apiService from '../services/api';
import toast from 'react-hot-toast';

const schema = yup.object({
  assetId: yup.number().required('Asset is required'),
  baseId: yup.number().required('Base is required'),
  quantity: yup.number().positive('Quantity must be positive').required('Quantity is required'),
  reason: yup.string().required('Reason is required'),
  expenditureDate: yup.string().required('Expenditure date is required'),
}).required();

type ExpenditureFormData = yup.InferType<typeof schema>;

const Expenditures: React.FC = () => {
  const [expenditures, setExpenditures] = useState<Expenditure[]>([]);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [bases, setBases] = useState<Base[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingExpenditure, setEditingExpenditure] = useState<Expenditure | null>(null);
  const [filters, setFilters] = useState<FilterOptions>({});

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ExpenditureFormData>({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    loadData();
  }, [filters]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [expendituresResponse, assetsResponse, basesResponse] = await Promise.all([
        apiService.getExpenditures(filters),
        apiService.getAssets(),
        apiService.getBases()
      ]);
      setExpenditures(expendituresResponse.data);
      setAssets(assetsResponse.data);
      setBases(basesResponse.data);
      console.log(expendituresResponse.data);
    } catch (error) {
      toast.error('Failed to load expenditures data');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: ExpenditureFormData) => {
    try {
      const expenditureData = {
        assetId: data.assetId,
        baseId: data.baseId,
        quantity: data.quantity,
        reason: data.reason,
        expenditureDate: data.expenditureDate,
      };
      if (editingExpenditure) {
        await apiService.updateExpenditure(editingExpenditure.id, expenditureData);
        toast.success('Expenditure updated successfully');
        loadData();
      } else {
        const response = await apiService.createExpenditure(expenditureData);
        toast.success('Expenditure recorded successfully');
        setExpenditures(prev => [response.data, ...prev]);
      }
      reset();
      setShowForm(false);
      setEditingExpenditure(null);
    } catch (error: any) {
      toast.error(error.response?.data || 'Failed to save expenditure');
    }
  };

  const handleEdit = (expenditure: Expenditure) => {
    setEditingExpenditure(expenditure);
    reset({
      assetId: expenditure.asset.id,
      baseId: expenditure.base.id,
      quantity: expenditure.quantity,
      reason: expenditure.reason,
      expenditureDate: expenditure.expenditureDate.split('T')[0],
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this expenditure?')) {
      try {
        await apiService.deleteExpenditure(id);
        toast.success('Expenditure deleted successfully');
        loadData();
      } catch (error) {
        toast.error('Failed to delete expenditure');
      }
    }
  };

  const handleCancel = () => {
    reset();
    setShowForm(false);
    setEditingExpenditure(null);
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
          <h1 className="text-2xl font-bold text-military-900">Expenditures</h1>
          <p className="text-military-600">Record and manage asset expenditures</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="btn-primary flex items-center space-x-2"
        >
          <PlusIcon className="h-5 w-5" />
          <span>Record Expenditure</span>
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
            <label className="block text-sm font-medium text-military-700 mb-1">Base</label>
            <select className="input-field" value={filters.baseId || ''} onChange={e => setFilters({ ...filters, baseId: e.target.value ? Number(e.target.value) : undefined })}>
              <option value="">All Bases</option>
              {bases.map(base => <option key={base.id} value={base.id}>{base.name}</option>)}
            </select>
          </div>
        </div>
      </div>

      {/* Expenditure Form */}
      {showForm && (
        <div className="card">
          <h3 className="text-lg font-medium text-military-900 mb-4">{editingExpenditure ? 'Edit Expenditure' : 'Record New Expenditure'}</h3>
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
                <label className="block text-sm font-medium text-military-700 mb-1">Base *</label>
                <select {...register('baseId')} className="input-field">
                  <option value="">Select Base</option>
                  {bases.map(base => <option key={base.id} value={base.id}>{base.name}</option>)}
                </select>
                {errors.baseId && <p className="mt-1 text-sm text-red-600">{errors.baseId.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">Quantity *</label>
                <input {...register('quantity')} type="number" min="1" className="input-field" />
                {errors.quantity && <p className="mt-1 text-sm text-red-600">{errors.quantity.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">Expenditure Date *</label>
                <input {...register('expenditureDate')} type="date" className="input-field" />
                {errors.expenditureDate && <p className="mt-1 text-sm text-red-600">{errors.expenditureDate.message}</p>}
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">Reason *</label>
              <textarea {...register('reason')} rows={3} className="input-field" />
              {errors.reason && <p className="mt-1 text-sm text-red-600">{errors.reason.message}</p>}
            </div>
            <div className="flex justify-end space-x-3">
              <button type="button" onClick={handleCancel} className="btn-secondary">Cancel</button>
              <button type="submit" className="btn-primary">{editingExpenditure ? 'Update Expenditure' : 'Record Expenditure'}</button>
            </div>
          </form>
        </div>
      )}

      {/* Expenditures Table */}
      <div className="card">
        <h3 className="text-lg font-medium text-military-900 mb-4">Expenditure History</h3>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-military-200">
            <thead className="bg-military-50">
              <tr>
                <th className="table-header">Date</th>
                <th className="table-header">Asset</th>
                <th className="table-header">Base</th>
                <th className="table-header">Quantity</th>
                <th className="table-header">Reason</th>
                <th className="table-header">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-military-200">
              {expenditures.map(expenditure => (
                <tr key={expenditure.id} className="hover:bg-military-50">
                  <td className="table-cell">{new Date(expenditure.expenditureDate).toLocaleDateString()}</td>
                  <td className="table-cell">{expenditure.asset.assetType.name} ({expenditure.asset.serialNumber})</td>
                  <td className="table-cell">{expenditure.base.name}</td>
                  <td className="table-cell">{expenditure.quantity}</td>
                  <td className="table-cell">{expenditure.reason}</td>
                  <td className="table-cell">
                    <div className="flex space-x-2">
                      <button onClick={() => handleEdit(expenditure)} className="text-primary-600 hover:text-primary-900"><PencilIcon className="h-4 w-4" /></button>
                      <button onClick={() => handleDelete(expenditure.id)} className="text-red-600 hover:text-red-900"><TrashIcon className="h-4 w-4" /></button>
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

export default Expenditures; 