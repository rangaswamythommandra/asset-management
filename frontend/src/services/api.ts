import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';
import type { 
  User, Base, AssetType, Asset, Purchase, Transfer, Assignment, 
  Expenditure, AuditLog, AuthResponse, LoginRequest, RegisterRequest,
  DashboardMetrics, FilterOptions 
} from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor to include auth token
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Add response interceptor to handle token refresh
    this.api.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;
        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;
          try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
              const response = await this.refreshToken(refreshToken);
              localStorage.setItem('token', response.data.token);
              localStorage.setItem('refreshToken', response.data.refreshToken);
              originalRequest.headers.Authorization = `Bearer ${response.data.token}`;
              return this.api(originalRequest);
            }
          } catch (refreshError) {
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
            window.location.href = '/login';
          }
        }
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async login(credentials: LoginRequest): Promise<AxiosResponse<AuthResponse>> {
    return this.api.post('/auth/login', credentials);
  }

  async register(userData: RegisterRequest): Promise<AxiosResponse<AuthResponse>> {
    return this.api.post('/auth/register', userData);
  }

  async refreshToken(refreshToken: string): Promise<AxiosResponse<AuthResponse>> {
    return this.api.post('/auth/refresh', { refreshToken });
  }

  // Dashboard endpoints
  async getDashboardMetrics(filters?: FilterOptions): Promise<AxiosResponse<DashboardMetrics>> {
    return this.api.get('/dashboard/metrics', { params: filters });
  }

  // User endpoints
  async getCurrentUser(): Promise<AxiosResponse<User>> {
    return this.api.get('/users/me');
  }

  async getUsers(): Promise<AxiosResponse<User[]>> {
    return this.api.get('/users');
  }

  // Base endpoints
  async getBases(): Promise<AxiosResponse<Base[]>> {
    return this.api.get('/bases');
  }

  async getBase(id: number): Promise<AxiosResponse<Base>> {
    return this.api.get(`/bases/${id}`);
  }

  async createBase(base: Partial<Base>): Promise<AxiosResponse<Base>> {
    return this.api.post('/bases', base);
  }

  async updateBase(id: number, base: Partial<Base>): Promise<AxiosResponse<Base>> {
    return this.api.put(`/bases/${id}`, base);
  }

  async deleteBase(id: number): Promise<AxiosResponse<void>> {
    return this.api.delete(`/bases/${id}`);
  }

  // Asset Type endpoints
  async getAssetTypes(): Promise<AxiosResponse<AssetType[]>> {
    return this.api.get('/asset-types');
  }

  async getAssetType(id: number): Promise<AxiosResponse<AssetType>> {
    return this.api.get(`/asset-types/${id}`);
  }

  async createAssetType(assetType: Partial<AssetType>): Promise<AxiosResponse<AssetType>> {
    return this.api.post('/asset-types', assetType);
  }

  async updateAssetType(id: number, assetType: Partial<AssetType>): Promise<AxiosResponse<AssetType>> {
    return this.api.put(`/asset-types/${id}`, assetType);
  }

  async deleteAssetType(id: number): Promise<AxiosResponse<void>> {
    return this.api.delete(`/asset-types/${id}`);
  }

  // Asset endpoints
  async getAssets(filters?: FilterOptions): Promise<AxiosResponse<Asset[]>> {
    return this.api.get('/assets', { params: filters });
  }

  async getAsset(id: number): Promise<AxiosResponse<Asset>> {
    return this.api.get(`/assets/${id}`);
  }

  async createAsset(asset: Partial<Asset>): Promise<AxiosResponse<Asset>> {
    return this.api.post('/assets', asset);
  }

  async updateAsset(id: number, asset: Partial<Asset>): Promise<AxiosResponse<Asset>> {
    return this.api.put(`/assets/${id}`, asset);
  }

  async deleteAsset(id: number): Promise<AxiosResponse<void>> {
    return this.api.delete(`/assets/${id}`);
  }

  // Purchase endpoints
  async getPurchases(filters?: FilterOptions): Promise<AxiosResponse<Purchase[]>> {
    return this.api.get('/purchases', { params: filters });
  }

  async getPurchase(id: number): Promise<AxiosResponse<Purchase>> {
    return this.api.get(`/purchases/${id}`);
  }

  async createPurchase(purchase: Partial<Purchase>): Promise<AxiosResponse<Purchase>> {
    return this.api.post('/purchases', purchase);
  }

  async updatePurchase(id: number, purchase: Partial<Purchase>): Promise<AxiosResponse<Purchase>> {
    return this.api.put(`/purchases/${id}`, purchase);
  }

  async deletePurchase(id: number): Promise<AxiosResponse<void>> {
    return this.api.delete(`/purchases/${id}`);
  }

  // Transfer endpoints
  async getTransfers(filters?: FilterOptions): Promise<AxiosResponse<Transfer[]>> {
    return this.api.get('/transfers', { params: filters });
  }

  async getTransfer(id: number): Promise<AxiosResponse<Transfer>> {
    return this.api.get(`/transfers/${id}`);
  }

  async createTransfer(transfer: Partial<Transfer>): Promise<AxiosResponse<Transfer>> {
    return this.api.post('/transfers', transfer);
  }

  async updateTransfer(id: number, transfer: Partial<Transfer>): Promise<AxiosResponse<Transfer>> {
    return this.api.put(`/transfers/${id}`, transfer);
  }

  async deleteTransfer(id: number): Promise<AxiosResponse<void>> {
    return this.api.delete(`/transfers/${id}`);
  }

  async approveTransfer(id: number): Promise<AxiosResponse<Transfer>> {
    return this.api.post(`/transfers/${id}/approve`);
  }

  async rejectTransfer(id: number, reason: string): Promise<AxiosResponse<Transfer>> {
    return this.api.post(`/transfers/${id}/reject`, { reason });
  }

  // Assignment endpoints
  async getAssignments(filters?: FilterOptions): Promise<AxiosResponse<Assignment[]>> {
    return this.api.get('/assignments', { params: filters });
  }

  async getAssignment(id: number): Promise<AxiosResponse<Assignment>> {
    return this.api.get(`/assignments/${id}`);
  }

  async createAssignment(assignment: Partial<Assignment>): Promise<AxiosResponse<Assignment>> {
    return this.api.post('/assignments', assignment);
  }

  async updateAssignment(id: number, assignment: Partial<Assignment>): Promise<AxiosResponse<Assignment>> {
    return this.api.put(`/assignments/${id}`, assignment);
  }

  async deleteAssignment(id: number): Promise<AxiosResponse<void>> {
    return this.api.delete(`/assignments/${id}`);
  }

  async returnAssignment(id: number): Promise<AxiosResponse<Assignment>> {
    return this.api.put(`/assignments/${id}/return`);
  }

  // Expenditure endpoints
  async getExpenditures(filters?: FilterOptions): Promise<AxiosResponse<Expenditure[]>> {
    return this.api.get('/expenditures', { params: filters });
  }

  async getExpenditure(id: number): Promise<AxiosResponse<Expenditure>> {
    return this.api.get(`/expenditures/${id}`);
  }

  async createExpenditure(expenditure: Partial<Expenditure>): Promise<AxiosResponse<Expenditure>> {
    return this.api.post('/expenditures', expenditure);
  }

  async updateExpenditure(id: number, expenditure: Partial<Expenditure>): Promise<AxiosResponse<Expenditure>> {
    return this.api.put(`/expenditures/${id}`, expenditure);
  }

  async deleteExpenditure(id: number): Promise<AxiosResponse<void>> {
    return this.api.delete(`/expenditures/${id}`);
  }

  // Audit Log endpoints
  async getAuditLogs(filters?: FilterOptions): Promise<AxiosResponse<AuditLog[]>> {
    return this.api.get('/audit-logs', { params: filters });
  }
}

export const apiService = new ApiService();
export default apiService; 