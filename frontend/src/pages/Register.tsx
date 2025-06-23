import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { ShieldCheckIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../contexts/AuthContext';
import type { Base } from '../types';
import apiService from '../services/api';

const schema = yup.object({
  username: yup.string().required('Username is required'),
  password: yup.string().min(6, 'Password must be at least 6 characters').required('Password is required'),
  confirmPassword: yup.string().oneOf([yup.ref('password')], 'Passwords must match').required('Confirm password is required'),
  role: yup.string().oneOf(['ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER']).required('Role is required'),
  baseId: yup.number().required('Base is required'),
}).required();

type RegisterFormData = yup.InferType<typeof schema>;

const Register: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [bases, setBases] = useState<Base[]>([]);
  const [loadingBases, setLoadingBases] = useState(true);
  const { register: registerUser, login } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    const loadBases = async () => {
      try {
        setLoadingBases(true);
        const response = await apiService.getBases();
        setBases(response.data);
      } catch (error) {
        console.error('Failed to load bases:', error);
        setError('Failed to load bases. Please check if the backend is running.');
      } finally {
        setLoadingBases(false);
      }
    };

    loadBases();
  }, []);

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true);
    setError('');
    
    try {
      const response = await apiService.register(data);
      const { token, refreshToken } = response.data;
      
      localStorage.setItem('token', token);
      localStorage.setItem('refreshToken', refreshToken);
      
      login(token, refreshToken);
      navigate('/dashboard');
    } catch (error: any) {
      console.error('Registration error:', error);
      if (error.response?.data) {
        setError(error.response.data);
      } else if (error.message) {
        setError(error.message);
      } else {
        setError('Registration failed. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-military-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <div className="mx-auto h-8 w-8 flex items-center justify-center rounded-full bg-primary-100">
            <ShieldCheckIcon className="h-5 w-5 text-primary-600" />
          </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-military-900">
            Military Asset Management
          </h2>
          <p className="mt-2 text-center text-sm text-military-600">
            Create your account
          </p>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">
                Username
              </label>
              <input
                {...register('username')}
                type="text"
                className="input-field"
                placeholder="Enter username"
              />
              {errors.username && (
                <p className="mt-1 text-sm text-red-600">{errors.username.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">
                Password
              </label>
              <input
                {...register('password')}
                type="password"
                className="input-field"
                placeholder="Enter password"
              />
              {errors.password && (
                <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">
                Confirm Password
              </label>
              <input
                {...register('confirmPassword')}
                type="password"
                className="input-field"
                placeholder="Confirm password"
              />
              {errors.confirmPassword && (
                <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">
                Role
              </label>
              <select {...register('role')} className="input-field">
                <option value="">Select Role</option>
                <option value="ADMIN">Admin</option>
                <option value="BASE_COMMANDER">Base Commander</option>
                <option value="LOGISTICS_OFFICER">Logistics Officer</option>
              </select>
              {errors.role && (
                <p className="mt-1 text-sm text-red-600">{errors.role.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-military-700 mb-1">
                Base
              </label>
              <select 
                {...register('baseId')} 
                className="input-field"
                disabled={loadingBases}
              >
                <option value="">
                  {loadingBases ? 'Loading bases...' : 'Select Base'}
                </option>
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
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md">
              {error}
            </div>
          )}

          <div>
            <button
              type="submit"
              disabled={isLoading || loadingBases}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? 'Creating account...' : 'Create Account'}
            </button>
          </div>

          <div className="text-center">
            <Link
              to="/login"
              className="font-medium text-primary-600 hover:text-primary-500"
            >
              Already have an account? Sign in
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register; 