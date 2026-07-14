import React, { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, Select, InputNumber,
  DatePicker, Switch, Space, Tag, message, Popconfirm, Typography, Row, Col, Tooltip, Upload
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, FileTextOutlined, CheckCircleOutlined, ClockCircleOutlined, TagsOutlined, CloseOutlined, UploadOutlined, DownloadOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { jobApi, jobSkillApi, skillApi, jobPositionApi, employerApi, adminCompanyApi, jobCertificateApi, jobEducationApi, certificateApi, educationLevelApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';
import { downloadJobTemplate } from '../utils/excelTemplates';

const { Title, Text } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';
const STATUS_LABELS = { 1: 'Đang tuyển', 2: 'Tạm dừng', 3: 'Đã hết hạn', 4: 'Đã đóng' };
const STATUS_COLORS = { 1: 'success', 2: 'warning', 3: 'default', 4: 'default' };

export default function EmployerDashboardPage() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingJob, setEditingJob] = useState(null);
  const [saving, setSaving] = useState(false);
  const { user } = useAuth();
  const [form] = Form.useForm();

  // Job skills modal state
  const [skillModalOpen, setSkillModalOpen] = useState(false);
  const [skillModalJob, setSkillModalJob] = useState(null);
  const [jobSkills, setJobSkills] = useState([]);
  const [allSkills, setAllSkills] = useState([]);
  const [skillsLoading, setSkillsLoading] = useState(false);
  const [addingSkillId, setAddingSkillId] = useState(null);

  // Job certificate/education requirement modal state
  const [reqModalOpen, setReqModalOpen] = useState(false);
  const [reqModalJob, setReqModalJob] = useState(null);
  const [jobCertificates, setJobCertificates] = useState([]);
  const [jobEducations, setJobEducations] = useState([]);
  const [allCertificates, setAllCertificates] = useState([]);
  const [allEducationLevels, setAllEducationLevels] = useState([]);
  const [reqLoading, setReqLoading] = useState(false);
  const [addingCertId, setAddingCertId] = useState(null);
  const [addingCertScore, setAddingCertScore] = useState('');
  const [addingEduId, setAddingEduId] = useState(null);

  const [importing, setImporting] = useState(false);
  const [jobPositions, setJobPositions] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [employerProfile, setEmployerProfile] = useState(null);
  const [provinces, setProvinces] = useState([]);

  useEffect(() => {
    jobPositionApi.getAll().then(r => { if (r.data.success) setJobPositions(r.data.data || []); }).catch(() => {});
    employerApi.getProfile().then(r => { if (r.data.success) setEmployerProfile(r.data.data); }).catch(() => {});
    adminCompanyApi.getAll().then(r => { if (r.data.success) setCompanies(r.data.data || []); }).catch(() => {});
    fetch('https://provinces.open-api.vn/api/p/')
      .then(r => r.json())
      .then(data => setProvinces(data || []))
      .catch(() => {});
  }, []);

  // Download file mẫu jobs — ghi chú chức danh và thông tin từ API
  const handleDownloadJobTemplate = () => {
    downloadJobTemplate({ companies, jobPositions }).catch(() => message.error('Tạo file mẫu thất bại'));
  };

  const handleImportJobs = async ({ file }) => {
    setImporting(true);
    try {
      const form = new FormData();
      form.append('file', file);
      const res = await jobApi.import(form);
      if (res.data.success) { message.success(res.data.message); loadJobs(); }
      else message.error(res.data.message);
    } catch { message.error('Import thất bại'); }
    finally { setImporting(false); }
    return false;
  };

  const loadJobs = () => {
    setLoading(true);
    jobApi.getAll()
      .then((res) => {
        if (res.data.success) {
          const all = res.data.data || [];
          // Lọc chỉ lấy job của employer đang đăng nhập
          const mine = user?.employerId ? all.filter(j => String(j.employerId) === String(user.employerId)) : all;
          setJobs(mine);
        }
      })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadJobs(); }, []);

  const openCreate = () => { setEditingJob(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (job) => {
    setEditingJob(job);
    form.setFieldsValue({
      ...job,
      minSalary: job.minSalary || null,
      maxSalary: job.maxSalary || null,
      postedAt: job.postedAt ? dayjs(job.postedAt) : null,
      expiredAt: job.expiredAt ? dayjs(job.expiredAt) : null,
      applicationDeadline: job.applicationDeadline ? dayjs(job.applicationDeadline) : null,
      companyId: job.companyId || null,
      jobPositionId: job.jobPositionId || null,
    });
    setModalOpen(true);
  };

  const onSave = async (values) => {
    setSaving(true);
    const payload = {
      ...values,
      minSalary: values.minSalary || 0,
      maxSalary: values.maxSalary || 0,
      postedAt: values.postedAt?.toISOString() || '',
      expiredAt: values.expiredAt?.toISOString() || '',
      applicationDeadline: values.applicationDeadline?.toISOString() || '',
      hiddenOnExpiry: values.hiddenOnExpiry ? 'true' : 'false',
      employerId: user?.employerId || '',
      companyId: values.companyId || employerProfile?.companyId || '',
      jobPositionId: values.jobPositionId || '',
    };
    try {
      const res = editingJob
        ? await jobApi.update({ ...payload, id: editingJob.id })
        : await jobApi.create(payload);
      if (res.data.success) {
        message.success(editingJob ? 'Cập nhật thành công' : 'Đăng tin thành công');
        setModalOpen(false);
        loadJobs();
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Có lỗi xảy ra');
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async (id) => {
    try {
      await jobApi.delete(id);
      message.success('Đã xóa tin tuyển dụng');
      loadJobs();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const openSkillModal = async (job) => {
    setSkillModalJob(job);
    setSkillModalOpen(true);
    setSkillsLoading(true);
    try {
      const [jobSkillsRes, allSkillsRes] = await Promise.all([
        jobSkillApi.getByJob(job.id),
        skillApi.getAll(),
      ]);
      if (jobSkillsRes.data.success) setJobSkills(jobSkillsRes.data.data || []);
      if (allSkillsRes.data.success) setAllSkills(allSkillsRes.data.data || []);
    } catch {
      message.error('Không thể tải kỹ năng');
    } finally {
      setSkillsLoading(false);
    }
  };

  const handleAddSkill = async (skillId) => {
    if (!skillId) return;
    try {
      const res = await jobSkillApi.add(skillModalJob.id, skillId);
      if (res.data.success) {
        const refreshed = await jobSkillApi.getByJob(skillModalJob.id);
        if (refreshed.data.success) setJobSkills(refreshed.data.data || []);
        setAddingSkillId(null);
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Thêm kỹ năng thất bại');
    }
  };

  const handleRemoveSkill = async (skillId) => {
    try {
      await jobSkillApi.remove(skillModalJob.id, skillId);
      setJobSkills(prev => prev.filter(s => s.skillId !== skillId && s.id !== skillId));
    } catch {
      message.error('Xóa kỹ năng thất bại');
    }
  };

  const openRequirementModal = async (job) => {
    setReqModalJob(job);
    setReqModalOpen(true);
    setReqLoading(true);
    try {
      const [jobCertRes, jobEduRes, allCertRes, allEduRes] = await Promise.all([
        jobCertificateApi.getByJob(job.id),
        jobEducationApi.getByJob(job.id),
        certificateApi.getAll(),
        educationLevelApi.getAll(),
      ]);
      if (jobCertRes.data.success) setJobCertificates(jobCertRes.data.data || []);
      if (jobEduRes.data.success) setJobEducations(jobEduRes.data.data || []);
      if (allCertRes.data.success) setAllCertificates(allCertRes.data.data || []);
      if (allEduRes.data.success) setAllEducationLevels(allEduRes.data.data || []);
    } catch {
      message.error('Không thể tải yêu cầu chứng chỉ/học vấn');
    } finally {
      setReqLoading(false);
    }
  };

  const handleAddCertificate = async (certificateId) => {
    if (!certificateId) return;
    try {
      const res = await jobCertificateApi.add(reqModalJob.id, certificateId, addingCertScore || null);
      if (res.data.success) {
        const refreshed = await jobCertificateApi.getByJob(reqModalJob.id);
        if (refreshed.data.success) setJobCertificates(refreshed.data.data || []);
        setAddingCertId(null);
        setAddingCertScore('');
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Thêm chứng chỉ thất bại');
    }
  };

  const handleRemoveCertificate = async (certificateId) => {
    try {
      await jobCertificateApi.remove(reqModalJob.id, certificateId);
      setJobCertificates(prev => prev.filter(c => c.certificateId !== certificateId));
    } catch {
      message.error('Xóa chứng chỉ thất bại');
    }
  };

  const handleAddEducation = async (educationLevelId) => {
    if (!educationLevelId) return;
    try {
      const res = await jobEducationApi.add(reqModalJob.id, educationLevelId);
      if (res.data.success) {
        const refreshed = await jobEducationApi.getByJob(reqModalJob.id);
        if (refreshed.data.success) setJobEducations(refreshed.data.data || []);
        setAddingEduId(null);
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Thêm học vấn thất bại');
    }
  };

  const handleRemoveEducation = async (educationLevelId) => {
    try {
      await jobEducationApi.remove(reqModalJob.id, educationLevelId);
      setJobEducations(prev => prev.filter(e => e.id !== educationLevelId));
    } catch {
      message.error('Xóa học vấn thất bại');
    }
  };

  const activeCount = jobs.filter(j => j.status === 1).length;

  const columns = [
    {
      title: 'Tiêu đề', dataIndex: 'title', key: 'title',
      render: (v) => <Text strong style={{ color: '#1a1a1a' }}>{v}</Text>
    },
    { title: 'Địa điểm', dataIndex: 'location', key: 'location', responsive: ['md'] },
    {
      title: 'Mức lương', key: 'salary', responsive: ['sm'],
      render: (_, r) => {
        const min = r.minSalary, max = r.maxSalary;
        if (!min && !max) return <Text type="secondary">Thỏa thuận</Text>;
        if (min && max) return <Text style={{ color: GREEN, fontWeight: 600 }}>{min.toLocaleString('vi-VN')} – {max.toLocaleString('vi-VN')} VNĐ</Text>;
        return <Text style={{ color: GREEN, fontWeight: 600 }}>{(min || max).toLocaleString('vi-VN')} VNĐ</Text>;
      }
    },
    { title: 'SL', dataIndex: 'quantity', key: 'quantity', responsive: ['lg'], width: 60, align: 'center' },
    {
      title: 'Trạng thái', dataIndex: 'status', key: 'status', width: 120,
      render: (v) => <Tag color={STATUS_COLORS[v]}>{STATUS_LABELS[v]}</Tag>
    },
    {
      title: 'Hạn nộp', dataIndex: 'applicationDeadline', key: 'deadline', responsive: ['xl'],
      render: (v) => v ? dayjs(v).format('DD/MM/YYYY') : '—'
    },
    {
      title: 'Thao tác', key: 'actions', fixed: 'right', width: 130,
      render: (_, record) => (
        <Space size={4}>
          <Tooltip title="Kỹ năng">
            <Button icon={<TagsOutlined />} size="small" onClick={() => openSkillModal(record)} style={{ borderRadius: 6 }} />
          </Tooltip>
          <Tooltip title="Chứng chỉ & học vấn yêu cầu">
            <Button icon={<SafetyCertificateOutlined />} size="small" onClick={() => openRequirementModal(record)} style={{ borderRadius: 6 }} />
          </Tooltip>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(record)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa tin này?" onConfirm={() => onDelete(record.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 60px' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>Bảng điều khiển</Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Quản lý tin tuyển dụng của bạn</Text>
        </div>
      </div>

      <div style={{ maxWidth: 1100, margin: '-32px auto 0', padding: '0 16px 32px' }}>
        {/* Stats */}
        <Row gutter={[12, 12]} style={{ marginBottom: 20 }}>
          {[
            { title: 'Tổng tin đăng', value: jobs.length, icon: <FileTextOutlined />, color: '#1677ff' },
            { title: 'Đang tuyển', value: activeCount, icon: <CheckCircleOutlined />, color: GREEN },
            { title: 'Tạm dừng / Đóng', value: jobs.length - activeCount, icon: <ClockCircleOutlined />, color: '#fa8c16' },
          ].map((s, i) => (
            <Col xs={8} key={i}>
              <div style={{ background: '#fff', borderRadius: 12, padding: '16px 20px', boxShadow: '0 2px 8px rgba(0,0,0,0.07)', textAlign: 'center' }}>
                <div style={{ fontSize: 22, color: s.color, marginBottom: 4 }}>{s.icon}</div>
                <div style={{ fontSize: 24, fontWeight: 700, color: '#1a1a1a' }}>{s.value}</div>
                <div style={{ fontSize: 12, color: '#888' }}>{s.title}</div>
              </div>
            </Col>
          ))}
        </Row>

        {/* Table card */}
        <div style={{ background: '#fff', borderRadius: 14, padding: '20px 20px', boxShadow: '0 2px 8px rgba(0,0,0,0.07)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap', gap: 8 }}>
            <Title level={5} style={{ margin: 0 }}>Danh sách tin tuyển dụng</Title>
            <Space wrap>
              <Tooltip title="Tải file mẫu Excel">
                <Button icon={<DownloadOutlined />} onClick={handleDownloadJobTemplate}>File mẫu</Button>
              </Tooltip>
              <Upload showUploadList={false} accept=".xlsx,.xls" customRequest={handleImportJobs} disabled={importing}>
                <Button icon={<UploadOutlined />} loading={importing}>Import Excel</Button>
              </Upload>
              <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
                style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
                Đăng tin mới
              </Button>
            </Space>
          </div>
          <Table columns={columns} dataSource={jobs} rowKey="id" loading={loading}
            scroll={{ x: 600 }} pagination={{ pageSize: 10, showSizeChanger: true }}
            rowClassName={() => 'table-row-hover'} />
        </div>
      </div>

      <Modal title={<span style={{ fontWeight: 700 }}>{editingJob ? 'Chỉnh sửa tin tuyển dụng' : 'Đăng tin tuyển dụng mới'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null} width={660}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="title" label={<span style={{ fontWeight: 600 }}>Tiêu đề công việc</span>} rules={[{ required: true }]}>
            <Select
              showSearch placeholder="Chọn hoặc nhập tiêu đề..."
              filterOption={(input, opt) => (opt?.children ?? '').toLowerCase().includes(input.toLowerCase())}
              style={{ borderRadius: 8 }}
              allowClear
              notFoundContent="Không tìm thấy chức danh"
            >
              {jobPositions.map(p => <Option key={p.id} value={p.name}>{p.name}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="description" label={<span style={{ fontWeight: 600 }}>Mô tả công việc</span>} rules={[{ required: true }]}>
            <Input.TextArea rows={4} placeholder="Mô tả chi tiết về công việc..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Row gutter={12}>
            <Col xs={24} sm={12}>
              <Form.Item name="companyId" label={<span style={{ fontWeight: 600 }}>Công ty</span>}>
                <Select
                  showSearch placeholder="Chọn công ty..."
                  filterOption={(input, opt) => (opt?.label ?? '').toLowerCase().includes(input.toLowerCase())}
                  style={{ borderRadius: 8 }}
                  allowClear
                  options={companies.map(c => ({ value: c.id, label: c.companyName }))}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="jobPositionId" label={<span style={{ fontWeight: 600 }}>Chức danh</span>}>
                <Select
                  showSearch placeholder="Chọn chức danh..."
                  filterOption={(input, opt) => (opt?.label ?? '').toLowerCase().includes(input.toLowerCase())}
                  style={{ borderRadius: 8 }}
                  allowClear
                  options={jobPositions.map(p => ({ value: p.id, label: p.name }))}
                />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="minSalary" label={<span style={{ fontWeight: 600 }}>Lương tối thiểu</span>}>
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder="10000000" min={0} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="maxSalary" label={<span style={{ fontWeight: 600 }}>Lương tối đa</span>}>
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder="20000000" min={0} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="currency" label={<span style={{ fontWeight: 600 }}>Tiền tệ</span>} initialValue="VND">
                <Select style={{ borderRadius: 8 }}>
                  <Option value="VND">VND</Option>
                  <Option value="USD">USD</Option>
                  <Option value="EUR">EUR</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="quantity" label={<span style={{ fontWeight: 600 }}>Số lượng</span>} rules={[{ required: true }]}>
                <InputNumber min={1} style={{ width: '100%', borderRadius: 8 }} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={14}>
              <Form.Item name="location" label={<span style={{ fontWeight: 600 }}>Địa điểm</span>} rules={[{ required: true }]}>
                <Select
                  showSearch
                  allowClear
                  placeholder="Chọn tỉnh / thành phố..."
                  filterOption={(input, option) =>
                    option.label.toLowerCase().includes(input.toLowerCase())
                  }
                  style={{ borderRadius: 8 }}
                  options={provinces.map(p => ({
                    value: p.name.replace(/^Thành phố\s+/i, '').replace(/^Tỉnh\s+/i, '').trim(),
                    label: p.name,
                  }))}
                />
              </Form.Item>
            </Col>
            <Col span={10}>
              <Form.Item name="experience" label={<span style={{ fontWeight: 600 }}>Kinh nghiệm</span>}>
                <Input placeholder="2 năm" style={{ borderRadius: 8 }} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col xs={24} sm={8}>
              <Form.Item name="postedAt" label={<span style={{ fontWeight: 600 }}>Ngày đăng</span>}>
                <DatePicker style={{ width: '100%', borderRadius: 8 }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8}>
              <Form.Item name="expiredAt" label={<span style={{ fontWeight: 600 }}>Ngày hết hạn</span>}>
                <DatePicker style={{ width: '100%', borderRadius: 8 }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8}>
              <Form.Item name="applicationDeadline" label={<span style={{ fontWeight: 600 }}>Hạn nộp hồ sơ</span>}>
                <DatePicker style={{ width: '100%', borderRadius: 8 }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12} align="middle">
            <Col flex={1}>
              <Form.Item name="status" label={<span style={{ fontWeight: 600 }}>Trạng thái</span>} initialValue={1}>
                <Select style={{ borderRadius: 8 }}>
                  <Option value={1}>Đang tuyển</Option>
                  <Option value={2}>Tạm dừng</Option>
                  <Option value={4}>Đóng</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col>
              <Form.Item name="hiddenOnExpiry" label={<span style={{ fontWeight: 600 }}>Ẩn khi hết hạn</span>} valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
          </Row>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              {editingJob ? 'Lưu thay đổi' : 'Đăng tin'}
            </Button>
          </div>
        </Form>
      </Modal>

      {/* Modal quản lý kỹ năng cho job */}
      <Modal
        title={<span style={{ fontWeight: 700 }}>Kỹ năng yêu cầu — {skillModalJob?.title}</span>}
        open={skillModalOpen}
        onCancel={() => { setSkillModalOpen(false); setAddingSkillId(null); }}
        footer={<Button onClick={() => { setSkillModalOpen(false); setAddingSkillId(null); }}>Đóng</Button>}
        width={480}
      >
        <div style={{ marginBottom: 16 }}>
          <div style={{ fontWeight: 600, marginBottom: 8, color: '#555' }}>Thêm kỹ năng</div>
          <Space.Compact style={{ width: '100%' }}>
            <Select
              placeholder="Chọn kỹ năng..."
              style={{ flex: 1 }}
              value={addingSkillId}
              onChange={setAddingSkillId}
              showSearch
              filterOption={(input, opt) => (opt?.children ?? '').toLowerCase().includes(input.toLowerCase())}
            >
              {allSkills
                .filter(s => !jobSkills.some(js => js.skillId === s.id))
                .map(s => <Option key={s.id} value={s.id}>{s.skillName}</Option>)}
            </Select>
            <Button type="primary" style={{ background: GREEN, borderColor: GREEN }}
              onClick={() => handleAddSkill(addingSkillId)} disabled={!addingSkillId}>
              Thêm
            </Button>
          </Space.Compact>
        </div>

        <div style={{ fontWeight: 600, marginBottom: 8, color: '#555' }}>Kỹ năng hiện tại</div>
        {skillsLoading ? (
          <div style={{ color: '#aaa', textAlign: 'center', padding: '16px 0' }}>Đang tải...</div>
        ) : jobSkills.length === 0 ? (
          <div style={{ color: '#aaa', textAlign: 'center', padding: '16px 0' }}>Chưa có kỹ năng nào</div>
        ) : (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
            {jobSkills.map(s => {
              const skill = allSkills.find(sk => sk.id === s.skillId);
              return (
                <Tag
                  key={s.skillId}
                  closable
                  onClose={() => handleRemoveSkill(s.skillId)}
                  closeIcon={<CloseOutlined />}
                  style={{ borderRadius: 20, padding: '4px 10px', fontSize: 13, background: '#f0fdf4', borderColor: '#bbf7d0', color: '#166534' }}
                >
                  {skill?.skillName ?? `Skill #${s.skillId}`}
                </Tag>
              );
            })}
          </div>
        )}
      </Modal>

      {/* Modal chứng chỉ & học vấn yêu cầu cho job */}
      <Modal
        title={<span style={{ fontWeight: 700 }}>Chứng chỉ & học vấn yêu cầu — {reqModalJob?.title}</span>}
        open={reqModalOpen}
        onCancel={() => { setReqModalOpen(false); setAddingCertId(null); setAddingCertScore(''); setAddingEduId(null); }}
        footer={<Button onClick={() => { setReqModalOpen(false); setAddingCertId(null); setAddingCertScore(''); setAddingEduId(null); }}>Đóng</Button>}
        width={520}
      >
        <div style={{ marginBottom: 20 }}>
          <div style={{ fontWeight: 600, marginBottom: 8, color: '#555' }}>Chứng chỉ yêu cầu</div>
          <Space.Compact style={{ width: '100%', marginBottom: 12 }}>
            <Select
              placeholder="Chọn chứng chỉ..."
              style={{ flex: 1.4 }}
              value={addingCertId}
              onChange={setAddingCertId}
              showSearch
              filterOption={(input, opt) => (opt?.children ?? '').toLowerCase().includes(input.toLowerCase())}
            >
              {allCertificates
                .filter(c => !jobCertificates.some(jc => jc.certificateId === c.id))
                .map(c => <Option key={c.id} value={c.id}>{c.certificatesName ?? c.certificateName}</Option>)}
            </Select>
            <Input
              placeholder="Điểm tối thiểu (VD: 750)"
              style={{ flex: 1 }}
              value={addingCertScore}
              onChange={(e) => setAddingCertScore(e.target.value)}
            />
            <Button type="primary" style={{ background: GREEN, borderColor: GREEN }}
              onClick={() => handleAddCertificate(addingCertId)} disabled={!addingCertId}>
              Thêm
            </Button>
          </Space.Compact>
          {reqLoading ? (
            <div style={{ color: '#aaa', textAlign: 'center', padding: '12px 0' }}>Đang tải...</div>
          ) : jobCertificates.length === 0 ? (
            <div style={{ color: '#aaa', textAlign: 'center', padding: '12px 0' }}>Chưa có chứng chỉ nào</div>
          ) : (
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {jobCertificates.map(c => (
                <Tag
                  key={c.certificateId}
                  closable
                  onClose={() => handleRemoveCertificate(c.certificateId)}
                  closeIcon={<CloseOutlined />}
                  style={{ borderRadius: 20, padding: '4px 10px', fontSize: 13, background: '#fff7e6', borderColor: '#ffd591', color: '#ad6800' }}
                >
                  {c.certificateName}{c.requiredScore ? ` (tối thiểu ${c.requiredScore})` : ''}
                </Tag>
              ))}
            </div>
          )}
        </div>

        <div>
          <div style={{ fontWeight: 600, marginBottom: 8, color: '#555' }}>Trình độ học vấn yêu cầu</div>
          <Space.Compact style={{ width: '100%', marginBottom: 12 }}>
            <Select
              placeholder="Chọn trình độ..."
              style={{ flex: 1 }}
              value={addingEduId}
              onChange={setAddingEduId}
              showSearch
              filterOption={(input, opt) => (opt?.children ?? '').toLowerCase().includes(input.toLowerCase())}
            >
              {allEducationLevels
                .filter(e => !jobEducations.some(je => je.id === e.id))
                .map(e => <Option key={e.id} value={e.id}>{e.levelName}</Option>)}
            </Select>
            <Button type="primary" style={{ background: GREEN, borderColor: GREEN }}
              onClick={() => handleAddEducation(addingEduId)} disabled={!addingEduId}>
              Thêm
            </Button>
          </Space.Compact>
          {reqLoading ? (
            <div style={{ color: '#aaa', textAlign: 'center', padding: '12px 0' }}>Đang tải...</div>
          ) : jobEducations.length === 0 ? (
            <div style={{ color: '#aaa', textAlign: 'center', padding: '12px 0' }}>Chưa có yêu cầu học vấn nào</div>
          ) : (
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {jobEducations.map(e => (
                <Tag
                  key={e.id}
                  closable
                  onClose={() => handleRemoveEducation(e.id)}
                  closeIcon={<CloseOutlined />}
                  style={{ borderRadius: 20, padding: '4px 10px', fontSize: 13, background: '#e6f4ff', borderColor: '#91caff', color: '#0958d9' }}
                >
                  {e.levelName}
                </Tag>
              ))}
            </div>
          )}
        </div>
      </Modal>
    </AppLayout>
  );
}
