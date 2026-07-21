import React, { useEffect, useState } from 'react';
import {
  Alert,
  Card,
  Empty,
  Space,
  Spin,
  Tag,
  Table,
  Typography,
  message,
} from 'antd';
import { CheckCircleOutlined, CrownOutlined } from '@ant-design/icons';
import AppLayout from '../components/AppLayout';
import { paymentApi } from '../api/services';
import { useAuth } from '../context/AuthContext';

const { Title, Paragraph, Text } = Typography;

const statusConfig = {
  SUCCESS: { color: 'success', label: 'Thành công' },
  FAILED: { color: 'error', label: 'Thất bại' },
  PENDING: { color: 'processing', label: 'Đang chờ' },
};

const roleBenefits = {
  CANDIDATE: {
    title: 'Quyền lợi VIP dành cho ứng viên',
    active: [
      'Gói VIP và thời hạn được kích hoạt sau khi Fake Bank xác nhận thành công.',
      'Danh sách việc làm có phần “Gợi ý cho bạn” bằng AI dựa trên kỹ năng và mức lương mong muốn.',
      'Bạn có thể tiếp tục cập nhật kỹ năng để AI cải thiện gợi ý việc làm.',
    ],
    unavailable: [
      'Ưu tiên xếp hạng hồ sơ hoặc đơn ứng tuyển chưa được bật trong bản demo.',
      'Badge VIP hiển thị công khai trên hồ sơ chưa được bật trong bản demo.',
    ],
  },
  EMPLOYER: {
    title: 'Quyền lợi VIP dành cho nhà tuyển dụng',
    active: [
      'Gói VIP và thời hạn được kích hoạt sau khi Fake Bank xác nhận thành công.',
      'Bạn có thể tiếp tục đăng tin, quản lý tin tuyển dụng và xem đơn ứng tuyển.',
      'Lịch sử thanh toán giúp theo dõi các lần mua gói và trạng thái xử lý.',
    ],
    unavailable: [
      'AI xếp hạng ứng viên theo từng tin tuyển dụng chưa được bật trong bản demo.',
      'Đẩy tin lên đầu danh sách hoặc Badge VIP công khai chưa được bật trong bản demo.',
    ],
  },
};

const formatDate = (value) => {
  if (!value) return '—';
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? '—' : date.toLocaleString('vi-VN');
};

function VipBenefitPanel({ role, vipStatus }) {
  const benefits = roleBenefits[role] || roleBenefits.CANDIDATE;
  return (
    <Card title={benefits.title}>
      <Alert
        type={vipStatus?.active ? 'success' : 'info'}
        showIcon
        icon={vipStatus?.active ? <CheckCircleOutlined /> : <CrownOutlined />}
        message={vipStatus?.active ? 'VIP đã được kích hoạt' : 'Sau khi thanh toán thành công'}
        description={vipStatus?.active
          ? `Gói ${vipStatus.packageName || 'VIP'} đang có hiệu lực${vipStatus.endDate ? ` đến ${vipStatus.endDate}` : ''}.`
          : 'Các quyền lợi sẽ được cập nhật sau khi Fake Bank xác nhận giao dịch thành công.'}
        style={{ marginBottom: 20 }}
      />
      <Title level={5}>Đã có trong hệ thống</Title>
      <ul>
        {benefits.active.map((benefit) => <li key={benefit}>{benefit}</li>)}
      </ul>
      <Title level={5} style={{ marginTop: 20 }}>Chưa bật trong bản demo</Title>
      <ul style={{ marginBottom: 0, color: '#777' }}>
        {benefits.unavailable.map((benefit) => <li key={benefit}>{benefit}</li>)}
      </ul>
    </Card>
  );
}

export default function PaymentHistoryPage() {
  const { user } = useAuth();
  const [history, setHistory] = useState([]);
  const [vipStatus, setVipStatus] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadHistory = async () => {
    setLoading(true);
    try {
      const [historyResponse, vipResponse] = await Promise.all([
        paymentApi.getHistory(),
        paymentApi.getVipStatus(),
      ]);
      if (!historyResponse.data.success) {
        throw new Error(historyResponse.data.message || 'Không thể tải lịch sử thanh toán');
      }
      setHistory(historyResponse.data.data || []);
      if (vipResponse.data.success) setVipStatus(vipResponse.data.data);
    } catch (error) {
      message.error(error.message || 'Không thể tải lịch sử thanh toán');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHistory();
  }, []);

  const columns = [
    {
      title: 'Mã giao dịch',
      dataIndex: 'txnRef',
      render: (value) => <Text code>{value ? `${value.slice(0, 13)}…` : '—'}</Text>,
    },
    { title: 'Gói dịch vụ', dataIndex: 'packageName', render: (value) => value || 'Gói VIP' },
    {
      title: 'Số tiền demo',
      dataIndex: 'amount',
      render: (value) => `${Number(value || 0).toLocaleString('vi-VN')} ₫`,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      render: (value) => {
        const config = statusConfig[value] || { color: 'default', label: value || 'Không rõ' };
        return <Tag color={config.color}>{config.label}</Tag>;
      },
    },
    { title: 'Tạo lúc', dataIndex: 'createdAt', render: formatDate },
    { title: 'Cập nhật lúc', dataIndex: 'updatedAt', render: formatDate },
  ];

  return (
    <AppLayout>
      <div style={{ maxWidth: 1180, margin: '0 auto', padding: '32px 20px 56px' }}>
        <Space direction="vertical" size={24} style={{ width: '100%' }}>
          <div>
            <Title level={2} style={{ marginBottom: 8 }}>Lịch sử thanh toán</Title>
            <Paragraph type="secondary" style={{ marginBottom: 0 }}>
              Các giao dịch của tài khoản hiện tại. Số tiền chỉ dùng cho cổng Fake Bank học tập.
            </Paragraph>
          </div>

          <VipBenefitPanel role={user?.role} vipStatus={vipStatus} />

          <Card>
            {loading ? (
              <div style={{ textAlign: 'center', padding: 48 }}><Spin size="large" /></div>
            ) : history.length === 0 ? (
              <Empty description="Chưa có giao dịch nào" />
            ) : (
              <Table
                rowKey="txnRef"
                columns={columns}
                dataSource={history}
                scroll={{ x: 850 }}
                pagination={{ pageSize: 8 }}
              />
            )}
          </Card>
        </Space>
      </div>
    </AppLayout>
  );
}
