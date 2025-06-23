import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { ShieldCheckIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../contexts/AuthContext';
import toast from 'react-hot-toast';
import type { Base } from '../types';
import apiService from '../services/api';

const schema = yup.object({
  username: yup.string().required('Username is required'),
  password: yup.string().required('Password is required'),
  baseId: yup.number().required('Base is required'),
}).required();

type LoginFormData = yup.InferType<typeof schema>;

const Login: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [bases, setBases] = useState<Base[]>([]);
  const [loadingBases, setLoadingBases] = useState(true);
  const { login } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
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

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);
    setError('');
    
    try {
      const response = await apiService.login({
        username: data.username,
        password: data.password
      });
      const { token, refreshToken } = response.data;
      
      await login(token, refreshToken);
      navigate('/dashboard');
    } catch (error: any) {
      console.error('Login error:', error);
      if (error.response?.data) {
        setError(error.response.data);
      } else if (error.message) {
        setError(error.message);
      } else {
        setError('Login failed. Please check your credentials and try again.');
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
            Sign in to your account
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
              {isLoading ? 'Signing in...' : 'Sign in'}
            </button>
          </div>

          <div className="text-center">
            <Link
              to="/register"
              className="font-medium text-primary-600 hover:text-primary-500"
            >
              Don't have an account? Register
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login; 