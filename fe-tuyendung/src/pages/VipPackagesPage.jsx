import React, { useEffect, useState } from 'react';
import {
  Card, Col, Row, Button, Tag, Typography, Spin, Empty, message, Space, Alert,
} from 'antd';
import {
  CrownOutlined, CheckCircleOutlined, SafetyCertificateOutlined,
  ClockCircleOutlined, RocketOutlined,
} from '@ant-design/icons';
import { paymentApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';

const { Title, Text, Paragraph } = Typography;
const GREEN = '#00b14f';
const GOLD = '#faad14';

function formatVipDate(value) {
  if (typeof value !== 'string' || !/^\d{4}-\d{2}-\d{2}$/.test(value)) return '—';
  const [year, month, day] = value.split('-');
  return `${day}/${month}/${year}`;
}

const BENEFIT_LABELS = {
  PRIORITY_APPLY: 'Ưu tiên hồ sơ ứng tuyển',
  FEATURED_JOB: 'Đẩy tin tuyển dụng nổi bật',
};

const ROLE_BENEFITS = {
  CANDIDATE: {
    active: [
      'Kích hoạt gói VIP sau khi Fake Bank xác nhận thành công',
      'Gợi ý việc làm bằng AI theo kỹ năng và mức lương mong muốn',
      'Cập nhật kỹ năng để cải thiện kết quả gợi ý',
    ],
    unavailable: [
      'Ưu tiên hồ sơ hoặc đơn ứng tuyển chưa bật trong bản demo',
      'Badge VIP công khai trên hồ sơ chưa bật trong bản demo',
    ],
  },
  EMPLOYER: {
    active: [
      'Kích hoạt gói VIP sau khi Fake Bank xác nhận thành công',
      'Đăng tin và quản lý đơn ứng tuyển như hiện tại',
      'Theo dõi thời hạn và lịch sử thanh toán của gói',
    ],
    unavailable: [
      'AI xếp hạng ứng viên chưa bật trong bản demo',
      'Đẩy tin nổi bật hoặc Badge VIP công khai chưa bật trong bản demo',
    ],
  },
};

export default function VipPackagesPage() {
  const { user } = useAuth();
  const [packages, setPackages] = useState([]);
  const [vipStatus, setVipStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [buyingId, setBuyingId] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const [pkgRes, vipRes] = await Promise.all([
        paymentApi.getPackages(),
        paymentApi.getVipStatus(),
      ]);
      if (pkgRes.data.success) setPackages(pkgRes.data.data || []);
      if (vipRes.data.success) setVipStatus(vipRes.data.data);
    } catch {
      message.error('Không tải được danh sách gói VIP');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleBuy = async (pkg) => {
    setBuyingId(pkg.id);
    try {
      const res = await paymentApi.createPayment(pkg.id);
      if (res.data.success && res.data.data?.paymentUrl) {
        message.loading('Đang chuyển tới checkout Fake-bank...', 1.5);
        window.location.href = res.data.data.paymentUrl;
      } else {
        message.error(res.data.message || 'Không tạo được phiên thanh toán');
      }
    } catch (err) {
      message.error(err.response?.data?.message || 'Lỗi khi tạo phiên thanh toán');
    } finally {
      setBuyingId(null);
    }
  };

  const formatPrice = (price) =>
    Number(price || 0).toLocaleString('vi-VN') + ' ₫';

  const benefits = ROLE_BENEFITS[user?.role] || ROLE_BENEFITS.CANDIDATE;

  return (
    <AppLayout>
      <div style={{ maxWidth: 1100, margin: '0 auto', padding: '24px 16px 48px' }}>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <CrownOutlined style={{ fontSize: 42, color: GOLD }} />
          <Title level={2} style={{ marginTop: 12, marginBottom: 8 }}>
            Gói VIP {user?.role === 'EMPLOYER' ? 'Nhà tuyển dụng' : 'Ứng viên'}
          </Title>
          <Paragraph type="secondary" style={{ fontSize: 16, maxWidth: 560, margin: '0 auto' }}>
            Thanh toán qua <strong>Fake-bank checkout</strong>. Không trừ tiền thật.
          </Paragraph>
        </div>

        {vipStatus?.active && (
          <Alert
            type="success"
            showIcon
            icon={<CheckCircleOutlined />}
            style={{ marginBottom: 24, borderRadius: 12 }}
            message={
              <span>
                Bạn đang dùng gói <strong>{vipStatus.packageName}</strong>
                {` — hết hạn ${formatVipDate(vipStatus.endDate)}`}
              </span>
            }
            description="Mua thêm gói sẽ được cộng dồn thời hạn VIP."
          />
        )}

        <Row gutter={[16, 16]} style={{ marginBottom: 32 }}>
          <Col xs={24} md={12}>
            <Card size="small" title="Đã có trong hệ thống" style={{ borderRadius: 12, height: '100%', borderColor: '#e8f9f0' }}>
              <ul style={{ margin: 0, paddingLeft: 20 }}>
                {benefits.active.map((benefit) => <li key={benefit}>{benefit}</li>)}
              </ul>
            </Card>
          </Col>
          <Col xs={24} md={12}>
            <Card size="small" title="Chưa bật trong bản demo" style={{ borderRadius: 12, height: '100%' }}>
              <ul style={{ margin: 0, paddingLeft: 20, color: '#777' }}>
                {benefits.unavailable.map((benefit) => <li key={benefit}>{benefit}</li>)}
              </ul>
            </Card>
          </Col>
        </Row>

        {loading ? (
          <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>
        ) : packages.length === 0 ? (
          <Empty description="Chưa có gói VIP phù hợp với tài khoản của bạn" />
        ) : (
          <Row gutter={[20, 20]} justify="center">
            {packages.map((pkg, idx) => {
              const popular = idx === packages.length - 1 || (packages.length > 1 && idx === 1);
              return (
                <Col xs={24} sm={12} md={10} lg={8} key={pkg.id}>
                  <Card
                    hoverable
                    style={{
                      borderRadius: 16,
                      height: '100%',
                      border: popular ? `2px solid ${GREEN}` : '1px solid #f0f0f0',
                      boxShadow: popular ? '0 8px 24px rgba(0,177,79,0.15)' : undefined,
                      position: 'relative',
                    }}
                    styles={{ body: { padding: 28 } }}
                  >
                    {popular && (
                      <Tag color={GREEN} style={{ position: 'absolute', top: 16, right: 16, borderRadius: 20 }}>
                        Phổ biến
                      </Tag>
                    )}
                    <Space direction="vertical" size={12} style={{ width: '100%' }}>
                      <Tag icon={<RocketOutlined />} color="gold" style={{ borderRadius: 20, width: 'fit-content' }}>
                        {BENEFIT_LABELS[pkg.benefitType] || pkg.benefitType || 'VIP'}
                      </Tag>
                      <Title level={4} style={{ margin: 0 }}>{pkg.packageName}</Title>
                      <div>
                        <span style={{ fontSize: 32, fontWeight: 800, color: GREEN }}>
                          {formatPrice(pkg.price)}
                        </span>
                      </div>
                      <Space>
                        <ClockCircleOutlined style={{ color: '#999' }} />
                        <Text type="secondary">{pkg.durationDays} ngày sử dụng</Text>
                      </Space>
                      <Paragraph type="secondary" style={{ minHeight: 48, marginBottom: 8 }}>
                        {pkg.description || 'Nâng cấp VIP để tận hưởng đặc quyền trên TopJob.'}
                      </Paragraph>
                      <ul style={{ paddingLeft: 18, margin: '0 0 16px', color: '#555' }}>
                        <li><SafetyCertificateOutlined style={{ color: GREEN, marginRight: 6 }} />Checkout thẻ test qua Fake-bank</li>
                        <li><CheckCircleOutlined style={{ color: GREEN, marginRight: 6 }} />Kích hoạt ngay sau thanh toán</li>
                        <li><CrownOutlined style={{ color: GOLD, marginRight: 6 }} />Cộng dồn nếu đang VIP</li>
                      </ul>
                      <Button
                        type="primary"
                        size="large"
                        block
                        icon={<CrownOutlined />}
                        loading={buyingId === pkg.id}
                        onClick={() => handleBuy(pkg)}
                        style={{
                          height: 48,
                          borderRadius: 10,
                          fontWeight: 700,
                          background: popular ? GREEN : undefined,
                        }}
                      >
                        Thanh toán
                      </Button>
                    </Space>
                  </Card>
                </Col>
              );
            })}
          </Row>
        )}
      </div>
    </AppLayout>
  );
}
