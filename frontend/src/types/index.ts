export interface User {
  id: number;
  username: string;
  role: 'ADMIN' | 'BASE_COMMANDER' | 'LOGISTICS_OFFICER';
  base?: Base;
  createdAt: string;
  updatedAt: string;
}

export interface Base {
  id: number;
  name: string;
  location: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AssetType {
  id: number;
  name: string;
  description?: string;
  category: string;
  createdAt: string;
  updatedAt: string;
}

export interface Asset {
  id: number;
  serialNumber: string;
  description?: string;
  assetType: AssetType;
  base: Base;
  status: 'AVAILABLE' | 'ASSIGNED' | 'MAINTENANCE' | 'RETIRED';
  purchaseDate: string;
  purchasePrice: number;
  currentValue: number;
  createdAt: string;
  updatedAt: string;
}

export interface Purchase {
  id: number;
  assetType: AssetType;
  base: Base;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
  purchaseDate: string;
  supplier: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Transfer {
  id: number;
  asset: Asset;
  fromBase: Base;
  toBase: Base;
  transferDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'COMPLETED' | 'REJECTED';
  approvedBy?: User;
  createdAt: string;
  updatedAt: string;
}

export interface Assignment {
  id: number;
  asset: Asset;
  assignedTo: User;
  assignedBy: User;
  assignmentDate: string;
  returnDate?: string;
  status: 'ACTIVE' | 'RETURNED' | 'EXPIRED';
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Expenditure {
  id: number;
  asset: Asset;
  base: Base;
  quantity: number;
  reason: string;
  expenditureDate: string;
  approvedBy?: User;
  createdAt?: string;
  updatedAt?: string;
}

export interface AuditLog {
  id: number;
  user: User;
  action: string;
  entity: string;
  entityId: number;
  details: string;
  timestamp: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  role: User['role'];
  baseId: number;
}

export interface DashboardMetrics {
  openingBalance: number;
  closingBalance: number;
  netMovement: number;
  purchases: number;
  transfersIn: number;
  transfersOut: number;
  assigned: number;
  expended: number;
}

export interface FilterOptions {
  dateFrom?: string;
  dateTo?: string;
  baseId?: number;
  assetTypeId?: number;
  status?: string;
  userId?: number;
} 