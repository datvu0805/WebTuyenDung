import React, { useEffect, useState } from 'react';
import { Result, Button, Spin, Typography, Card, Alert } from 'antd';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { paymentApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';

const { Text } = Typography;
const GREEN = '#00b14f';
const MAX_POLLS = 10;

export default function PaymentResultPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [transaction, setTransaction] = useState(null);
  const [vipStatus, setVipStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [timedOut, setTimedOut] = useState(false);

  const txnRef = params.get('txnRef') || '';

  useEffect(() => {
    if (!txnRef) {
      setLoading(false);
      return undefined;
    }

    let polls = 0;
    let timer;
    let active = true;

    const loadStatus = async () => {
      try {
        const res = await paymentApi.getTransactionStatus(txnRef);
        if (!active) return;
        const data = res.data.success ? res.data.data : null;
        setTransaction(data);
        setLoading(false);
        if (data?.status === 'SUCCESS') {
          const vipRes = await paymentApi.getVipStatus();
          if (active && vipRes.data.success) setVipStatus(vipRes.data.data);
          return;
        }
        if (data?.status === 'PENDING' && polls < MAX_POLLS) {
          polls += 1;
          timer = window.setTimeout(loadStatus, 2000);
        } else if (data?.status === 'PENDING') {
          setTimedOut(true);
        }
      } catch {
        if (active) {
          setTransaction(null);
          setLoading(false);
        }
      }
    };

    loadStatus();
    return () => {
      active = false;
      window.clearTimeout(timer);
    };
  }, [txnRef]);

  const home = user?.role === 'EMPLOYER' ? '/employer/dashboard' : '/jobs';
  const status = transaction?.status;
  const resultStatus = status === 'SUCCESS'
    ? 'success'
    : status === 'FAILED'
      ? 'error'
      : status === 'CANCELLED'
        ? 'warning'
        : !transaction
          ? 'error'
          : 'info';
  const title = status === 'SUCCESS'
    ? 'Thanh toán thành công'
    : status === 'FAILED'
      ? 'Thanh toán thất bại'
      : status === 'CANCELLED'
        ? 'Đã hủy thanh toán'
        : !transaction
          ? 'Không tìm thấy giao dịch'
          : timedOut
            ? 'Đang chờ xác nhận thanh toán'
            : 'Đang xử lý thanh toán';

  return (
    <AppLayout>
      <div style={{ maxWidth: 640, margin: '40px auto', padding: '0 16px' }}>
        {loading ? (
          <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>
        ) : (
          <Card style={{ borderRadius: 16 }}>
            <Result
              status={resultStatus}
              title={title}
              subTitle={(
                <div>
                  {!txnRef && <div>Thiếu mã giao dịch.</div>}
                  {txnRef && <div>Mã giao dịch: <Text code>{txnRef}</Text></div>}
                  {transaction?.packageName && <div>Gói: <strong>{transaction.packageName}</strong></div>}
                  {transaction?.amount != null && (
                    <div>Số tiền: {Number(transaction.amount).toLocaleString('vi-VN')} ₫</div>
                  )}
                  {status === 'SUCCESS' && vipStatus?.active && (
                    <Alert
                      type="success"
                      showIcon
                      style={{ marginTop: 16, textAlign: 'left' }}
                      message={user?.role === 'EMPLOYER' ? 'VIP nhà tuyển dụng đã kích hoạt' : 'VIP ứng viên đã kích hoạt'}
                      description={user?.role === 'EMPLOYER'
                        ? 'Bạn có thể tiếp tục đăng tin, quản lý đơn ứng tuyển và theo dõi thời hạn gói trong lịch sử thanh toán.'
                        : 'Bạn có thể xem Gợi ý cho bạn bằng AI trong danh sách việc làm; kết quả dựa trên kỹ năng và mức lương mong muốn.'}
                    />
                  )}
                  {status === 'PENDING' && (
                    <div style={{ marginTop: 12 }}>Backend chưa nhận được kết quả cuối cùng. Bạn có thể kiểm tra lại sau.</div>
                  )}
                </div>
              )}
              extra={[
                <Button
                  type="primary"
                  key="vip"
                  onClick={() => navigate('/vip')}
                  style={{ background: GREEN }}
                >
                  Xem gói VIP
                </Button>,
                <Button
                  key="history"
                  onClick={() => navigate('/payment/history')}
                >
                  Xem lịch sử thanh toán
                </Button>,
                <Button key="home" onClick={() => navigate(home)}>
                  Về trang chính
                </Button>,
              ]}
            />
          </Card>
        )}
      </div>
    </AppLayout>
  );
}
