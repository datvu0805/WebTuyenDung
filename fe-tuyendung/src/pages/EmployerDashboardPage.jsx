import React, { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, Select, InputNumber,
  DatePicker, Switch, Space, Tag, message, Popconfirm, Typography, Row, Col, Tooltip
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, FileTextOutlined, CheckCircleOutlined, ClockCircleOutlined, TagsOutlined, CloseOutlined } from '@ant-design/icons';
import { jobApi, jobSkillApi, skillApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';
const STATUS_LABELS = ['Đang tuyển', 'Tạm dừng', 'Đã đóng'];
const STATUS_COLORS = ['success', 'warning', 'default'];

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

  const loadJobs = () => {
    setLoading(true);
    jobApi.getAll()
      .then((res) => { if (res.data.success) setJobs(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadJobs(); }, []);

  const openCreate = () => { setEditingJob(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (job) => {
    setEditingJob(job);
    form.setFieldsValue({
      ...job,
      postedAt: job.postedAt ? dayjs(job.postedAt) : null,
      expiredAt: job.expiredAt ? dayjs(job.expiredAt) : null,
      applicationDeadline: job.applicationDeadline ? dayjs(job.applicationDeadline) : null,
    });
    setModalOpen(true);
  };

  const onSave = async (values) => {
    setSaving(true);
    const payload = {
      ...values,
      postedAt: values.postedAt?.toISOString() || '',
      expiredAt: values.expiredAt?.toISOString() || '',
      applicationDeadline: values.applicationDeadline?.toISOString() || '',
      hiddenOnExpiry: values.hiddenOnExpiry ? 'true' : 'false',
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

  const activeCount = jobs.filter(j => j.status === 0).length;

  const columns = [
    {
      title: 'Tiêu đề', dataIndex: 'title', key: 'title',
      render: (v) => <Text strong style={{ color: '#1a1a1a' }}>{v}</Text>
    },
    { title: 'Địa điểm', dataIndex: 'location', key: 'location', responsive: ['md'] },
    {
      title: 'Mức lương', dataIndex: 'salary', key: 'salary', responsive: ['sm'],
      render: (v) => v ? <Text style={{ color: GREEN, fontWeight: 600 }}>{v.toLocaleString('vi-VN')} VNĐ</Text> : <Text type="secondary">Thỏa thuận</Text>
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
            <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
              style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
              Đăng tin mới
            </Button>
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
            <Input placeholder="Lập trình viên Java Senior" style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="description" label={<span style={{ fontWeight: 600 }}>Mô tả công việc</span>} rules={[{ required: true }]}>
            <Input.TextArea rows={4} placeholder="Mô tả chi tiết về công việc..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Row gutter={12}>
            <Col span={14}>
              <Form.Item name="salary" label={<span style={{ fontWeight: 600 }}>Mức lương (VNĐ)</span>}>
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder="15000000" min={0} />
              </Form.Item>
            </Col>
            <Col span={10}>
              <Form.Item name="quantity" label={<span style={{ fontWeight: 600 }}>Số lượng</span>} rules={[{ required: true }]}>
                <InputNumber min={1} style={{ width: '100%', borderRadius: 8 }} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={14}>
              <Form.Item name="location" label={<span style={{ fontWeight: 600 }}>Địa điểm</span>} rules={[{ required: true }]}>
                <Input placeholder="Hà Nội" style={{ borderRadius: 8 }} />
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
              <Form.Item name="status" label={<span style={{ fontWeight: 600 }}>Trạng thái</span>} initialValue={0}>
                <Select style={{ borderRadius: 8 }}>
                  <Option value={0}>Đang tuyển</Option>
                  <Option value={1}>Tạm dừng</Option>
                  <Option value={2}>Đóng</Option>
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
    </AppLayout>
  );
}
