import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { PlusIcon, PencilIcon, TrashIcon, ArrowUturnLeftIcon } from '@heroicons/react/24/outline';
import type { Assignment, Asset, User, FilterOptions } from '../types';
import apiService from '../services/api';
import toast from 'react-hot-toast';
import { useAuth } from '../contexts/AuthContext';

const schema = yup.object({
  assetId: yup.number().required('Asset is required'),
  assignedToId: yup.number().required('Personnel is required'),
  assignmentDate: yup.string().required('Assignment date is required'),
  notes: yup.string().nullable().default(''),
}).required();

type AssignmentFormData = yup.InferType<typeof schema>;

const Assignments: React.FC = () => {
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingAssignment, setEditingAssignment] = useState<Assignment | null>(null);
  const [filters, setFilters] = useState<FilterOptions>({});
  const { user } = useAuth();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AssignmentFormData>({
    resolver: yupResolver(schema),
    defaultValues: { notes: '' },
  });

  useEffect(() => {
    loadData();
  }, [filters]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [assignmentsResponse, assetsResponse, usersResponse] = await Promise.all([
        apiService.getAssignments(filters),
        apiService.getAssets(),
        apiService.getUsers()
      ]);
      setAssignments(assignmentsResponse.data);
      setAssets(assetsResponse.data);
      setUsers(usersResponse.data);
    } catch (error) {
      toast.error('Failed to load assignments data');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: AssignmentFormData) => {
    try {
      const assignmentData = {
        assetId: data.assetId,
        assignedToId: data.assignedToId,
        assignmentDate: data.assignmentDate,
        notes: data.notes,
      };
      if (editingAssignment) {
        await apiService.updateAssignment(editingAssignment.id, assignmentData);
        toast.success('Assignment updated successfully');
      } else {
        await apiService.createAssignment(assignmentData);
        toast.success('Assignment created successfully');
      }
      reset();
      setShowForm(false);
      setEditingAssignment(null);
      loadData();
    } catch (error: any) {
      toast.error(error.response?.data || 'Failed to save assignment');
    }
  };

  const handleEdit = (assignment: Assignment) => {
    setEditingAssignment(assignment);
    reset({
      assetId: assignment.asset.id,
      assignedToId: assignment.assignedTo.id,
      assignmentDate: assignment.assignmentDate.split('T')[0],
      notes: assignment.notes || '',
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this assignment?')) {
      try {
        await apiService.deleteAssignment(id);
        toast.success('Assignment deleted successfully');
        loadData();
      } catch (error) {
        toast.error('Failed to delete assignment');
      }
    }
  };

  const handleReturn = async (id: number) => {
    if (window.confirm('Mark this assignment as returned?')) {
      try {
        await apiService.returnAssignment(id);
        toast.success('Assignment marked as returned');
        loadData();
      } catch (error) {
        toast.error('Failed to mark as returned');
      }
    }
  };

  const handleCancel = () => {
    reset();
    setShowForm(false);
    setEditingAssignment(null);
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
          <h1 className="text-2xl font-bold text-military-900">Assignments</h1>
          <p className="text-military-600">Assign assets to personnel and manage assignments</p>
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="btn-primary flex items-center space-x-2"
        >
          <PlusIcon className="h-5 w-5" />
          <span>Assign Asset</span>
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
        </div>
      </div>

      {/* Assignment Form */}
      {showForm && (
        <div className="card">
          <h3 className="text-lg font-medium text-military-900 mb-4">{editingAssignment ? 'Edit Assignment' : 'Assign Asset'}</h3>
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
                <label className="block text-sm font-medium text-military-700 mb-1">Personnel *</label>
                <select {...register('assignedToId')} className="input-field">
                  <option value="">Select Personnel</option>
                  {users.map(u => <option key={u.id} value={u.id}>{u.username} ({u.role})</option>)}
                </select>
                {errors.assignedToId && <p className="mt-1 text-sm text-red-600">{errors.assignedToId.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-military-700 mb-1">Assignment Date *</label>
                <input {...register('assignmentDate')} type="date" className="input-field" />
                {errors.assignmentDate && <p className="mt-1 text-sm text-red-600">{errors.assignmentDate.message}</p>}
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">Notes</label>
              <textarea {...register('notes')} rows={3} className="input-field" />
            </div>
            <div className="flex justify-end space-x-3">
              <button type="button" onClick={handleCancel} className="btn-secondary">Cancel</button>
              <button type="submit" className="btn-primary">{editingAssignment ? 'Update Assignment' : 'Assign Asset'}</button>
            </div>
          </form>
        </div>
      )}

      {/* Assignments Table */}
      <div className="card">
        <h3 className="text-lg font-medium text-military-900 mb-4">Assignment History</h3>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-military-200">
            <thead className="bg-military-50">
              <tr>
                <th className="table-header">Date</th>
                <th className="table-header">Asset</th>
                <th className="table-header">Personnel</th>
                <th className="table-header">Status</th>
                <th className="table-header">Notes</th>
                <th className="table-header">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-military-200">
              {assignments.map(assignment => (
                <tr key={assignment.id} className="hover:bg-military-50">
                  <td className="table-cell">{new Date(assignment.assignmentDate).toLocaleDateString()}</td>
                  <td className="table-cell">{assignment.asset.assetType.name} ({assignment.asset.serialNumber})</td>
                  <td className="table-cell">{assignment.assignedTo.username}</td>
                  <td className="table-cell">{assignment.status}</td>
                  <td className="table-cell">{assignment.notes}</td>
                  <td className="table-cell">
                    <div className="flex space-x-2">
                      {assignment.status === 'ACTIVE' && (
                        <button onClick={() => handleReturn(assignment.id)} className="text-green-600 hover:text-green-900"><ArrowUturnLeftIcon className="h-4 w-4" /></button>
                      )}
                      <button onClick={() => handleEdit(assignment)} className="text-primary-600 hover:text-primary-900"><PencilIcon className="h-4 w-4" /></button>
                      <button onClick={() => handleDelete(assignment.id)} className="text-red-600 hover:text-red-900"><TrashIcon className="h-4 w-4" /></button>
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

export default Assignments; 