import { useNavigate } from 'react-router-dom';
import { 
  CurrencyDollarIcon, 
  ArrowUpIcon, 
  UserGroupIcon,
  ShoppingCartIcon,
  ArrowPathIcon
} from '@heroicons/react/24/outline';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import type { DashboardMetrics, FilterOptions, Base, AssetType } from '../types';
import apiService from '../services/api';
import toast from 'react-hot-toast';

interface DashboardProps {}

interface NetMovementDetails {
  purchases: number;
  transfersIn: number;
  transfersOut: number;
}

export default function Dashboard({}: DashboardProps) {
  const navigate = useNavigate();
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [loading, setLoading] = useState(true);
  const [bases, setBases] = useState<Base[]>([]);
  const [assetTypes, setAssetTypes] = useState<AssetType[]>([]);
  const [filters, setFilters] = useState<FilterOptions>({});
  const [showNetMovementModal, setShowNetMovementModal] = useState(false);
  const [netMovementDetails, setNetMovementDetails] = useState<NetMovementDetails | null>(null);

  useEffect(() => {
    loadData();
  }, [filters]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [metricsResponse, basesResponse, assetTypesResponse] = await Promise.all([
        apiService.getDashboardMetrics(filters),
        apiService.getBases(),
        apiService.getAssetTypes()
      ]);
      
      setMetrics(metricsResponse.data);
      setBases(basesResponse.data);
      setAssetTypes(assetTypesResponse.data);
    } catch (error) {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleRecordPurchase = () => {
    navigate('/purchases');
  };

  const handleCreateTransfer = () => {
    navigate('/transfers');
  };

  const handleAssignAsset = () => {
    navigate('/assignments');
  };

  const handleNetMovementClick = async () => {
    try {
      const [purchasesRes, transfersRes] = await Promise.all([
        apiService.getPurchases(),
        apiService.getTransfers()
      ]);
      
      const purchases = purchasesRes.data;
      const transfers = transfersRes.data;
      
      const transfersIn = transfers.filter(t => t.status === 'COMPLETED').length;
      const transfersOut = transfers.filter(t => t.status === 'COMPLETED').length;
      
      setNetMovementDetails({
        purchases: purchases.length,
        transfersIn,
        transfersOut
      });
      setShowNetMovementModal(true);
    } catch (error) {
      console.error('Error loading net movement details:', error);
    }
  };

  const chartData = [
    { name: 'Opening Balance', value: metrics?.openingBalance || 0, color: '#3b82f6' },
    { name: 'Purchases', value: metrics?.purchases || 0, color: '#10b981' },
    { name: 'Transfers In', value: metrics?.transfersIn || 0, color: '#f59e0b' },
    { name: 'Transfers Out', value: metrics?.transfersOut || 0, color: '#ef4444' },
    { name: 'Closing Balance', value: metrics?.closingBalance || 0, color: '#8b5cf6' },
  ];

  const statusData = [
    { name: 'Assigned', value: metrics?.assigned || 0, color: '#3b82f6' },
    { name: 'Expended', value: metrics?.expended || 0, color: '#ef4444' },
    { name: 'Available', value: (metrics?.closingBalance || 0) - (metrics?.assigned || 0) - (metrics?.expended || 0), color: '#10b981' },
  ];

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
      <div>
        <h1 className="text-2xl font-bold text-military-900">Dashboard</h1>
        <p className="text-military-600">Overview of military asset management</p>
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

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="card">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <CurrencyDollarIcon className="h-8 w-8 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">Opening Balance</p>
              <p className="text-2xl font-bold text-gray-900">
                ${metrics?.openingBalance?.toLocaleString() || 0}
              </p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <CurrencyDollarIcon className="h-8 w-8 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">Closing Balance</p>
              <p className="text-2xl font-bold text-gray-900">
                ${metrics?.closingBalance?.toLocaleString() || 0}
              </p>
            </div>
          </div>
        </div>

        <div 
          className="card cursor-pointer hover:shadow-md transition-shadow"
          onClick={handleNetMovementClick}
        >
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <ArrowUpIcon className="h-8 w-8 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">Net Movement</p>
              <p className="text-2xl font-bold text-gray-900">
                ${metrics?.netMovement?.toLocaleString() || 0}
              </p>
              <p className="text-xs text-gray-500">Click for details</p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <UserGroupIcon className="h-8 w-8 text-purple-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">Assigned</p>
              <p className="text-2xl font-bold text-gray-900">
                {metrics?.assigned?.toLocaleString() || 0}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Net Movement Details Modal */}
      {showNetMovementModal && netMovementDetails && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold">Net Movement Details</h2>
              <button 
                onClick={() => setShowNetMovementModal(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>
            
            <div className="space-y-4">
              <div className="flex justify-between">
                <span>Purchases:</span>
                <span className="font-semibold text-green-600">+{netMovementDetails.purchases}</span>
              </div>
              <div className="flex justify-between">
                <span>Transfers In:</span>
                <span className="font-semibold text-blue-600">+{netMovementDetails.transfersIn}</span>
              </div>
              <div className="flex justify-between">
                <span>Transfers Out:</span>
                <span className="font-semibold text-red-600">-{netMovementDetails.transfersOut}</span>
              </div>
              <hr />
              <div className="flex justify-between font-bold">
                <span>Net Movement:</span>
                <span className={(metrics?.netMovement || 0) >= 0 ? 'text-green-600' : 'text-red-600'}>
                  {(metrics?.netMovement || 0) >= 0 ? '+' : ''}{metrics?.netMovement || 0}
                </span>
              </div>
            </div>
            
            <button 
              onClick={() => setShowNetMovementModal(false)}
              className="mt-6 w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600"
            >
              Close
            </button>
          </div>
        </div>
      )}

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Asset Movement Overview</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip formatter={(value) => `$${value?.toLocaleString()}`} />
              <Bar dataKey="value" fill="#3b82f6" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Asset Status Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={statusData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {statusData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => value?.toLocaleString()} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="card">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <button className="btn-primary flex items-center justify-center space-x-2" onClick={handleRecordPurchase}>
            <ShoppingCartIcon className="h-5 w-5" />
            <span>Record Purchase</span>
          </button>
          <button className="btn-primary flex items-center justify-center space-x-2" onClick={handleCreateTransfer}>
            <ArrowPathIcon className="h-5 w-5" />
            <span>Create Transfer</span>
          </button>
          <button className="btn-primary flex items-center justify-center space-x-2" onClick={handleAssignAsset}>
            <UserGroupIcon className="h-5 w-5" />
            <span>Assign Asset</span>
          </button>
        </div>
      </div>
    </div>
  );
} 