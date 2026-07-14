import React, { useEffect, useState } from 'react';
import {
  Tag, Button, Spin, Typography, Divider,
  message, Modal, Form, Input, Select, Badge,
} from 'antd';
import {
  EnvironmentOutlined, DollarOutlined, TeamOutlined,
  ClockCircleOutlined, ArrowLeftOutlined, SendOutlined,
  CalendarOutlined, BankOutlined, SolutionOutlined,
  CheckCircleOutlined, FileTextOutlined, EyeOutlined,
  StarOutlined, MessageOutlined,
} from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import { jobApi, applicationApi, cvApi, adminCompanyApi, favoriteJobApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/vi';

dayjs.extend(relativeTime);
dayjs.locale('vi');

const { Title, Text, Paragraph } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';

const STATUS_MAP = {
  1: { label: 'Đang tuyển',  bg: '#e8f9f0', color: '#00b14f' },
  2: { label: 'Tạm dừng',    bg: '#fff7e6', color: '#fa8c16' },
  3: { label: 'Đã hết hạn',  bg: '#f5f5f5', color: '#999'    },
  4: { label: 'Đã đóng',     bg: '#f5f5f5', color: '#999'    },
};

const LOGO_COLORS = ['#e8f9f0','#e6f4ff','#fff7e6','#f9f0ff','#fff1f0','#f0fff4'];
const LOGO_TEXT   = ['#00b14f','#1677ff','#fa8c16','#722ed1','#f5222d','#389e0d'];
function colorIdx(id = 0) { return id % LOGO_COLORS.length; }

function getInitials(name = '') {
  const w = name.trim().split(/\s+/);
  if (w.length >= 2) return (w[0][0] + w[1][0]).toUpperCase();
  return name.trim().slice(0, 2).toUpperCase() || 'CT';
}

function InfoCard({ icon, label, value, highlight }) {
  return (
    <div style={{
      background: '#fff', borderRadius: 10, padding: '14px 18px',
      border: '1px solid #eee', display: 'flex', gap: 12, alignItems: 'flex-start',
    }}>
      <div style={{ color: highlight ? GREEN : '#aaa', fontSize: 18, paddingTop: 2 }}>{icon}</div>
      <div>
        <div style={{ fontSize: 12, color: '#aaa', marginBottom: 2 }}>{label}</div>
        <div style={{ fontSize: 14, fontWeight: 600, color: highlight ? GREEN : '#1a1a1a' }}>{value}</div>
      </div>
    </div>
  );
}

// Modal xem thông tin công ty
function CompanyModal({ company, jobs, open, onClose }) {
  if (!company) return null;
  const ci = colorIdx(company.id);
  const activeJobs = jobs.filter(j => j.status === 1);

  return (
    <Modal
      open={open}
      onCancel={onClose}
      footer={null}
      width={640}
      title={null}
      styles={{ body: { padding: 0 } }}
    >
      {/* Header gradient */}
      <div style={{
        background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)',
        borderRadius: '8px 8px 0 0', padding: '28px 28px 20px',
      }}>
        <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
          <div style={{
            width: 64, height: 64, borderRadius: 12,
            background: 'rgba(255,255,255,0.2)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontWeight: 700, fontSize: 22, color: '#fff', flexShrink: 0,
          }}>
            {getInitials(company.companyName)}
          </div>
          <div>
            <div style={{ fontWeight: 700, fontSize: 20, color: '#fff' }}>{company.companyName}</div>
            <div style={{ color: 'rgba(255,255,255,0.8)', fontSize: 13, marginTop: 4 }}>
              <SolutionOutlined style={{ marginRight: 6 }} />
              {jobs.length} vị trí tuyển dụng
              {activeJobs.length > 0 && (
                <span style={{ marginLeft: 10, background: 'rgba(255,255,255,0.2)', borderRadius: 10, padding: '1px 8px' }}>
                  {activeJobs.length} đang tuyển
                </span>
              )}
            </div>
          </div>
        </div>
      </div>

      <div style={{ padding: '20px 28px 24px' }}>
        {/* Mô tả công ty */}
        {company.description && (
          <div style={{ marginBottom: 20 }}>
            <div style={{ fontWeight: 600, fontSize: 14, color: '#333', marginBottom: 8 }}>
              <FileTextOutlined style={{ marginRight: 6, color: GREEN }} />Giới thiệu
            </div>
            <Paragraph style={{ color: '#555', fontSize: 14, lineHeight: 1.7, margin: 0 }}>
              {company.description}
            </Paragraph>
          </div>
        )}

        <Divider style={{ margin: '12px 0 16px' }} />

        {/* Danh sách jobs */}
        <div style={{ fontWeight: 600, fontSize: 14, color: '#333', marginBottom: 12 }}>
          <EyeOutlined style={{ marginRight: 6, color: GREEN }} />Các vị trí đang tuyển dụng
        </div>
        {jobs.length === 0 ? (
          <div style={{ color: '#aaa', textAlign: 'center', padding: '20px 0' }}>Hiện không có vị trí nào</div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8, maxHeight: 320, overflowY: 'auto' }}>
            {jobs.map(j => {
              const st = STATUS_MAP[j.status] ?? STATUS_MAP[4];
              return (
                <div key={j.id} style={{
                  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                  padding: '12px 14px', borderRadius: 8, border: '1px solid #f0f0f0',
                  background: j.status === 1 ? '#fafffe' : '#fafafa', gap: 8, flexWrap: 'wrap',
                }}>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontWeight: 600, fontSize: 13, color: '#1a1a1a',
                      overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {j.title}
                    </div>
                    <div style={{ fontSize: 12, color: '#888', marginTop: 2 }}>
                      <EnvironmentOutlined style={{ marginRight: 4 }} />{j.location}
                      {(j.minSalary || j.maxSalary) && (
                        <span style={{ marginLeft: 10, color: GREEN, fontWeight: 500 }}>
                          {(j.minSalary || 0).toLocaleString('vi-VN')}
                          {j.maxSalary ? ' – ' + j.maxSalary.toLocaleString('vi-VN') : ''} VNĐ
                        </span>
                      )}
                    </div>
                  </div>
                  <span style={{
                    background: st.bg, color: st.color,
                    borderRadius: 4, padding: '2px 8px', fontSize: 11, fontWeight: 600, flexShrink: 0,
                  }}>
                    {st.label}
                  </span>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </Modal>
  );
}

export default function JobDetailPage() {
  const { id } = useParams();
  const [job, setJob]           = useState(null);
  const [loading, setLoading]   = useState(true);
  const [applyOpen, setApplyOpen]     = useState(false);
  const [submitting, setSubmitting]   = useState(false);
  const [cvList, setCvList]           = useState([]);
  const [cvLoading, setCvLoading]     = useState(false);
  const [isFavorite, setIsFavorite]   = useState(false);
  const [favoriteLoading, setFavoriteLoading] = useState(false);

  const [companyModal, setCompanyModal] = useState(false);
  const [company, setCompany]           = useState(null);
  const [companyJobs, setCompanyJobs]   = useState([]);
  const [companyLoading, setCompanyLoading] = useState(false);

  const { user } = useAuth();
  const navigate = useNavigate();
  const [form] = Form.useForm();

  useEffect(() => {
    jobApi.getById(id)
      .then((res) => {
        const data = res.data?.data || res.data;
        if (data) setJob(data);
        else message.error('Không tìm thấy tin tuyển dụng');
      })
      .catch(() => message.error('Không tìm thấy tin tuyển dụng'))
      .finally(() => setLoading(false));
  }, [id]);

  useEffect(() => {
    if (user?.role !== 'CANDIDATE' || !user?.candidateId) return;
    favoriteJobApi.check(user.candidateId, id)
      .then((res) => { if (res.data.success) setIsFavorite(!!res.data.data); })
      .catch(() => {});
  }, [id, user]);

  const handleToggleFavorite = async () => {
    setFavoriteLoading(true);
    try {
      if (isFavorite) await favoriteJobApi.remove(id);
      else await favoriteJobApi.add(id);
      setIsFavorite(!isFavorite);
    } catch {
      message.error('Có lỗi khi cập nhật yêu thích');
    } finally {
      setFavoriteLoading(false);
    }
  };

  const openCompanyModal = async () => {
    if (!job?.companyId) return;
    setCompanyModal(true);
    setCompanyLoading(true);
    try {
      const [companyRes, jobsRes] = await Promise.all([
        adminCompanyApi.getAll(),
        jobApi.getByCompany(job.companyId),
      ]);
      if (companyRes.data.success) {
        const found = (companyRes.data.data || []).find(c => c.id === job.companyId);
        setCompany(found || null);
      }
      if (jobsRes.data.success) {
        setCompanyJobs(jobsRes.data.data?.content || []);
      }
    } catch {
      message.error('Không thể tải thông tin công ty');
    } finally {
      setCompanyLoading(false);
    }
  };

  const openApplyModal = () => {
    setApplyOpen(true);
    if (user?.candidateId && cvList.length === 0) {
      setCvLoading(true);
      cvApi.getByCandidate(user.candidateId)
        .then((res) => { if (res.data.success) setCvList(res.data.data?.cvList || []); })
        .catch(() => {})
        .finally(() => setCvLoading(false));
    }
  };

  const handleApply = async (values) => {
    setSubmitting(true);
    try {
      const res = await applicationApi.submit({
        candidateID: user.candidateId, jobID: id, ...values,
      });
      if (res.data.success) {
        message.success('Nộp đơn thành công!');
        setApplyOpen(false);
        form.resetFields();
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Có lỗi xảy ra khi nộp đơn');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <AppLayout><div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div></AppLayout>;
  if (!job) return <AppLayout><div style={{ textAlign: 'center', padding: 40 }}>Không tìm thấy tin tuyển dụng</div></AppLayout>;

  const st = STATUS_MAP[job.status] ?? STATUS_MAP[4];
  const isActive = job.status === 1;
  const ci = colorIdx(job.companyId || job.id);
  const salary = (job.minSalary || job.maxSalary)
    ? `${(job.minSalary || 0).toLocaleString('vi-VN')}${job.maxSalary ? ' – ' + job.maxSalary.toLocaleString('vi-VN') : ''} ${job.currency || 'VNĐ'}`
    : 'Thỏa thuận';

  return (
    <AppLayout>
      <div style={{ maxWidth: 900, margin: '0 auto', padding: '24px 16px 48px' }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/jobs')}
          style={{ marginBottom: 20, borderRadius: 8 }}>
          Quay lại danh sách
        </Button>

        {/* ── Header card ── */}
        <div style={{
          background: '#fff', borderRadius: 14, padding: '28px 28px 24px',
          marginBottom: 14, border: '1px solid #eee', boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
        }}>
          <div style={{ display: 'flex', gap: 18, alignItems: 'flex-start', flexWrap: 'wrap' }}>
            {/* Logo */}
            <div style={{
              width: 68, height: 68, borderRadius: 12, flexShrink: 0,
              background: LOGO_COLORS[ci], color: LOGO_TEXT[ci],
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontWeight: 700, fontSize: 22,
            }}>
              {getInitials(job.companyName || job.title)}
            </div>

            <div style={{ flex: 1, minWidth: 200 }}>
              {/* Title + badge */}
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap', marginBottom: 6 }}>
                <Title level={3} style={{ margin: 0 }}>{job.title}</Title>
                <span style={{
                  background: st.bg, color: st.color,
                  borderRadius: 5, padding: '3px 12px', fontSize: 13, fontWeight: 600,
                }}>
                  {st.label}
                </span>
              </div>

              {/* Company clickable */}
              {job.companyName && (
                <div
                  onClick={openCompanyModal}
                  style={{
                    display: 'inline-flex', alignItems: 'center', gap: 6,
                    cursor: 'pointer', marginBottom: 10,
                    color: GREEN, fontWeight: 600, fontSize: 14,
                    borderBottom: '1px dashed #b7ebd0', paddingBottom: 1,
                  }}
                >
                  <BankOutlined />
                  {job.companyName}
                  <EyeOutlined style={{ fontSize: 12, opacity: 0.6 }} />
                </div>
              )}

              {/* Meta pills */}
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px 20px' }}>
                <Text style={{ fontSize: 14, color: '#555' }}>
                  <EnvironmentOutlined style={{ marginRight: 4, color: '#aaa' }} />
                  {job.location || 'Chưa cập nhật'}
                </Text>
                <Text style={{ fontSize: 14, color: GREEN, fontWeight: 600 }}>
                  <DollarOutlined style={{ marginRight: 4 }} />{salary}
                </Text>
                <Text style={{ fontSize: 14, color: '#555' }}>
                  <TeamOutlined style={{ marginRight: 4, color: '#aaa' }} />
                  {job.quantity} vị trí
                </Text>
                {job.experience && (
                  <Text style={{ fontSize: 14, color: '#555' }}>
                    <StarOutlined style={{ marginRight: 4, color: '#aaa' }} />
                    {job.experience}
                  </Text>
                )}
              </div>
            </div>

            {/* Apply + Chat buttons */}
            {user?.role === 'CANDIDATE' && isActive && (
              <div style={{ display: 'flex', gap: 8, flexShrink: 0 }}>
                <Button type="primary" icon={<SendOutlined />} size="large" onClick={openApplyModal}
                  style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600, height: 44 }}>
                  Ứng tuyển ngay
                </Button>
                <Button icon={<StarOutlined style={{ color: isFavorite ? '#faad14' : undefined }} />} size="large"
                  loading={favoriteLoading} onClick={handleToggleFavorite}
                  style={{ borderRadius: 8, height: 44 }}>
                  {isFavorite ? 'Đã lưu' : 'Lưu'}
                </Button>
                {job.employerUserId && (
                  <Button icon={<MessageOutlined />} size="large"
                    onClick={() => navigate(`/messages?with=${job.employerUserId}&name=${encodeURIComponent(job.companyName || 'Nhà tuyển dụng')}`)}
                    style={{ borderRadius: 8, height: 44, borderColor: GREEN, color: GREEN }}>
                    Nhắn tin
                  </Button>
                )}
              </div>
            )}
          </div>
        </div>

        {/* ── Info grid ── */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(185px, 1fr))', gap: 10, marginBottom: 14 }}>
          <InfoCard icon={<DollarOutlined />} label="Mức lương" value={salary} highlight />
          <InfoCard icon={<CalendarOutlined />} label="Hạn nộp hồ sơ"
            value={job.applicationDeadline ? dayjs(job.applicationDeadline).format('DD/MM/YYYY') : '—'} />
          <InfoCard icon={<CalendarOutlined />} label="Ngày hết hạn"
            value={job.expiredAt ? dayjs(job.expiredAt).format('DD/MM/YYYY') : '—'} />
          <InfoCard icon={<ClockCircleOutlined />} label="Ngày đăng"
            value={job.postedAt ? dayjs(job.postedAt).fromNow() : '—'} />
          <InfoCard icon={<TeamOutlined />} label="Số lượng tuyển"
            value={`${job.quantity} người`} />
          <InfoCard icon={<StarOutlined />} label="Kinh nghiệm"
            value={job.experience || 'Không yêu cầu'} />
          {job.hiddenOnExpiry !== undefined && (
            <InfoCard icon={<CheckCircleOutlined />} label="Ẩn khi hết hạn"
              value={job.hiddenOnExpiry ? 'Có' : 'Không'} />
          )}
        </div>

        {/* ── Description ── */}
        <div style={{
          background: '#fff', borderRadius: 14, padding: '24px 28px',
          border: '1px solid #eee', boxShadow: '0 1px 4px rgba(0,0,0,0.05)',
        }}>
          <Title level={5} style={{ marginBottom: 14, color: '#333' }}>
            <FileTextOutlined style={{ marginRight: 8, color: GREEN }} />Mô tả công việc
          </Title>
          <Paragraph style={{ whiteSpace: 'pre-wrap', lineHeight: 1.85, color: '#444', fontSize: 14, margin: 0 }}>
            {job.description || 'Chưa có mô tả công việc.'}
          </Paragraph>

          {/* Apply CTA bottom */}
          {user?.role === 'CANDIDATE' && isActive && (
            <div style={{ marginTop: 24, paddingTop: 20, borderTop: '1px solid #f0f0f0', display: 'flex', gap: 10, justifyContent: 'center' }}>
              <Button type="primary" icon={<SendOutlined />} size="large" onClick={openApplyModal}
                style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600, height: 46, paddingInline: 32 }}>
                Nộp đơn ứng tuyển
              </Button>
              {job.employerUserId && (
                <Button icon={<MessageOutlined />} size="large"
                  onClick={() => navigate(`/messages?with=${job.employerUserId}&name=${encodeURIComponent(job.companyName || 'Nhà tuyển dụng')}`)}
                  style={{ borderRadius: 8, height: 46, borderColor: GREEN, color: GREEN, paddingInline: 24 }}>
                  Nhắn tin với NTD
                </Button>
              )}
            </div>
          )}
        </div>
      </div>

      {/* ── Company Modal ── */}
      {companyLoading ? (
        <Modal open={companyModal} onCancel={() => setCompanyModal(false)} footer={null} width={480}>
          <div style={{ textAlign: 'center', padding: 40 }}><Spin size="large" /></div>
        </Modal>
      ) : (
        <CompanyModal
          company={company}
          jobs={companyJobs}
          open={companyModal}
          onClose={() => setCompanyModal(false)}
        />
      )}

      {/* ── Apply Modal ── */}
      <Modal title={<span style={{ fontWeight: 700 }}>Nộp đơn ứng tuyển</span>}
        open={applyOpen} onCancel={() => setApplyOpen(false)} footer={null} width={520}>
        <Form form={form} layout="vertical" onFinish={handleApply} style={{ marginTop: 16 }}>
          <Form.Item name="cvID" label={<span style={{ fontWeight: 600 }}>Chọn CV</span>}
            rules={[{ required: true, message: 'Vui lòng chọn CV' }]}>
            <Select placeholder="Chọn CV của bạn" loading={cvLoading} style={{ borderRadius: 8 }}
              notFoundContent={cvLoading ? 'Đang tải...' : 'Bạn chưa có CV. Hãy tải CV lên trước.'}>
              {cvList.map((cv) => (
                <Option key={cv.id} value={cv.id}>
                  {cv.cvTitle || cv.cv_title}{cv.version ? ` (v${cv.version})` : ''}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="coverLetter" label={<span style={{ fontWeight: 600 }}>Thư xin việc</span>}
            rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input.TextArea rows={4} placeholder="Giới thiệu bản thân và lý do bạn muốn ứng tuyển..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="description" label={<span style={{ fontWeight: 600 }}>Ghi chú thêm</span>}>
            <Input.TextArea rows={2} placeholder="Thông tin bổ sung (không bắt buộc)" style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setApplyOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={submitting}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              Nộp đơn
            </Button>
          </div>
        </Form>
      </Modal>
    </AppLayout>
  );
}
