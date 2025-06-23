import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import type { SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import type { Purchase, Base, AssetType, FilterOptions } from '../types';
import apiService from '../services/api';
import toast from 'react-hot-toast';

const schema = yup.object({
  assetTypeId: yup.number().required('Asset type is required'),
  baseId: yup.number().required('Base is required'),
  quantity: yup.number().positive('Quantity must be positive').required('Quantity is required'),
  unitPrice: yup.number().positive('Unit price must be positive').required('Unit price is required'),
  purchaseDate: yup.string().required('Purchase date is required'),
  supplier: yup.string().required('Supplier is required'),
  description: yup.string().required().default(''),
}).required();

type PurchaseFormData = yup.InferType<typeof schema>;

const Purchases: React.FC = () => {
  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [bases, setBases] = useState<Base[]>([]);
  const [assetTypes, setAssetTypes] = useState<AssetType[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingPurchase, setEditingPurchase] = useState<Purchase | null>(null);
  const [filters, setFilters] = useState<FilterOptions>({});

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    watch,
  } = useForm<PurchaseFormData>({
    resolver: yupResolver(schema),
  });

  const quantity = watch('quantity', 0);
  const unitPrice = watch('unitPrice', 0);

  useEffect(() => {
    loadData();
  }, [filters]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [purchasesResponse, basesResponse, assetTypesResponse] = await Promise.all([
        apiService.getPurchases(filters),
        apiService.getBases(),
        apiService.getAssetTypes()
      ]);
      
      setPurchases(purchasesResponse.data);
      setBases(basesResponse.data);
      setAssetTypes(assetTypesResponse.data);
    } catch (error) {
      toast.error('Failed to load purchases data');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit: SubmitHandler<PurchaseFormData> = async (data) => {
    try {
      if (editingPurchase) {
        await apiService.updatePurchase(editingPurchase.id, data);
        toast.success('Purchase updated successfully');
      } else {
        await apiService.createPurchase(data);
        toast.success('Purchase recorded successfully');
      }
      reset();
      setShowForm(false);
      setEditingPurchase(null);
      loadData();
    } catch (error: any) {
      toast.error(error.response?.data || 'Failed to save purchase');
    }
  };

  const handleEdit = (purchase: Purchase) => {
    setEditingPurchase(purchase);
    reset({
      assetTypeId: purchase.assetType.id,
      baseId: purchase.base.id,
      quantity: purchase.quantity,
      unitPrice: purchase.unitPrice,
      purchaseDate: purchase.purchaseDate.split('T')[0],
      supplier: purchase.supplier,
      description: purchase.description || '',
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this purchase?')) {
      try {
        await apiService.deletePurchase(id);
        toast.success('Purchase deleted successfully');
        loadData();
      } catch (error) {
        toast.error('Failed to delete purchase');
      }
    }
  };

  const handleCancel = () => {
    reset();
    setShowForm(false);
    setEditingPurchase(null);
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
          <h1 className="text-2xl font-bold text-military-900">Purchases</h1>
          <p className="text-military-600">Record and manage asset purchases</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="btn-primary flex items-center space-x-2"
        >
          <PlusIcon className="h-5 w-5" />
          <span>Record Purchase</span>
        </button>
      </div>

      {/* Filters */}
      <div className="card">
        <h3 className="text-lg font-medium text-military-900 mb-4">Filters</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">
              Date From
            </label>
            <input
              type="date"
              className="input-field"
              value={filters.dateFrom || ''}
              onChange={(e) => setFilters({ ...filters, dateFrom: e.target.value })}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">
              Date To
            </label>
            <input
              type="date"
              className="input-field"
              value={filters.dateTo || ''}
              onChange={(e) => setFilters({ ...filters, dateTo: e.target.value })}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">
              Base
            </label>
            <select
              className="input-field"
              value={filters.baseId || ''}
              onChange={(e) => setFilters({ ...filters, baseId: e.target.value ? Number(e.target.value) : undefined })}
            >
              <option value="">All Bases</option>
              {bases.map((base) => (
                <option key={base.id} value={base.id}>
                  {base.name}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-military-700 mb-1">
              Equipment Type
            </label>
            <select
              className="input-field"
              value={filters.assetTypeId || ''}
              onChange={(e) => setFilters({ ...filters, assetTypeId: e.target.value ? Number(e.target.value) : undefined })}
            >
              <option value="">All Types</option>
              {assetTypes.map((type) => (
                <option key={type.id} value={type.id}>
                  {type.name}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Purchase Form */}
      {showForm && (
        <div className="card">
          <h3 className="text-lg font-medium text-military-900 mb-4">
            {editingPurchase ? 'Edit Purchase' : 'Record New Purchase'}
          </h3>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">
                  Asset Type *
                </label>
                <select
                  {...register('assetTypeId')}
                  className="input-field"
                >
                  <option value="">Select Asset Type</option>
                  {assetTypes.map((type) => (
                    <option key={type.id} value={type.id}>
                      {type.name}
                    </option>
                  ))}
                </select>
                {errors.assetTypeId && (
                  <p className="mt-1 text-sm text-red-600">{errors.assetTypeId.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">
                  Base *
                </label>
                <select
                  {...register('baseId')}
                  className="input-field"
                >
                  <option value="">Select Base</option>
                  {bases.map((base) => (
                    <option key={base.id} value={base.id}>
                      {base.name}
                    </option>
                  ))}
                </select>
                {errors.baseId && (
                  <p className="mt-1 text-sm text-red-600">{errors.baseId.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">
                  Quantity *
                </label>
                <input
                  {...register('quantity')}
                  type="number"
                  min="1"
                  className="input-field"
                />
                {errors.quantity && (
                  <p className="mt-1 text-sm text-red-600">{errors.quantity.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">
                  Unit Price ($) *
                </label>
                <input
                  {...register('unitPrice')}
                  type="number"
                  min="0.01"
                  step="0.01"
                  className="input-field"
                />
                {errors.unitPrice && (
                  <p className="mt-1 text-sm text-red-600">{errors.unitPrice.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">
                  Purchase Date *
                </label>
                <input
                  {...register('purchaseDate')}
                  type="date"
                  className="input-field"
                />
                {errors.purchaseDate && (
                  <p className="mt-1 text-sm text-red-600">{errors.purchaseDate.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">
                  Supplier *
                </label>
                <input
                  {...register('supplier')}
                  type="text"
                  className="input-field"
                />
                {errors.supplier && (
                  <p className="mt-1 text-sm text-red-600">{errors.supplier.message}</p>
                )}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">
                Description
              </label>
              <textarea
                {...register('description')}
                rows={3}
                className="input-field"
              />
            </div>

            <div className="bg-military-50 p-4 rounded-lg">
              <p className="text-sm font-medium text-military-700">
                Total Amount: ${(quantity * unitPrice).toLocaleString()}
              </p>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={handleCancel}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="btn-primary"
              >
                {editingPurchase ? 'Update Purchase' : 'Record Purchase'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Purchases Table */}
      <div className="card">
        <h3 className="text-lg font-medium text-military-900 mb-4">Purchase History</h3>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-military-200">
            <thead className="bg-military-50">
              <tr>
                <th className="table-header">Date</th>
                <th className="table-header">Asset Type</th>
                <th className="table-header">Base</th>
                <th className="table-header">Quantity</th>
                <th className="table-header">Unit Price</th>
                <th className="table-header">Total Amount</th>
                <th className="table-header">Supplier</th>
                <th className="table-header">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-military-200">
              {purchases.map((purchase) => (
                <tr key={purchase.id} className="hover:bg-military-50">
                  <td className="table-cell">
                    {new Date(purchase.purchaseDate).toLocaleDateString()}
                  </td>
                  <td className="table-cell">{purchase.assetType.name}</td>
                  <td className="table-cell">{purchase.base.name}</td>
                  <td className="table-cell">{purchase.quantity}</td>
                  <td className="table-cell">${purchase.unitPrice.toLocaleString()}</td>
                  <td className="table-cell">${purchase.totalAmount.toLocaleString()}</td>
                  <td className="table-cell">{purchase.supplier}</td>
                  <td className="table-cell">
                    <div className="flex space-x-2">
                      <button
                        onClick={() => handleEdit(purchase)}
                        className="text-primary-600 hover:text-primary-900"
                      >
                        <PencilIcon className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(purchase.id)}
                        className="text-red-600 hover:text-red-900"
                      >
                        <TrashIcon className="h-4 w-4" />
                      </button>
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

export default Purchases; 