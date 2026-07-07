import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, Space, Popconfirm, message, Typography, Tag } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, BulbOutlined } from '@ant-design/icons';
import { skillApi } from '../api/services';
import AppLayout from '../components/AppLayout';

const { Title, Text } = Typography;
const GREEN = '#00b14f';

const TAG_COLORS = ['blue', 'geekblue', 'purple', 'cyan', 'teal', 'volcano', 'orange'];

export default function SkillManagePage() {
  const [skills, setSkills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    skillApi.getAll()
      .then((res) => { if (res.data.success) setSkills(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (skill) => { setEditing(skill); form.setFieldsValue({ skillName: skill.skillName }); setModalOpen(true); };

  const onSave = async ({ skillName }) => {
    setSaving(true);
    try {
      const res = editing ? await skillApi.update(editing.id, skillName) : await skillApi.create(skillName);
      if (res.data.success) {
        message.success(editing ? 'Cập nhật thành công' : 'Thêm thành công');
        setModalOpen(false);
        load();
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
      await skillApi.delete(id);
      message.success('Đã xóa kỹ năng');
      load();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const columns = [
    {
      title: 'Kỹ năng', dataIndex: 'skillName', key: 'skillName',
      render: (v, _, idx) => (
        <Tag color={TAG_COLORS[idx % TAG_COLORS.length]} style={{ borderRadius: 6, padding: '3px 10px', fontSize: 13 }}>
          {v}
        </Tag>
      )
    },
    { title: 'ID', dataIndex: 'id', key: 'id', width: 70, render: (v) => <Text style={{ color: '#bbb', fontSize: 13 }}>#{v}</Text> },
    {
      title: 'Thao tác', key: 'actions', width: 100, align: 'right',
      render: (_, record) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(record)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa kỹ năng này?" onConfirm={() => onDelete(record.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 56px' }}>
        <div style={{ maxWidth: 800, margin: '0 auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{ width: 40, height: 40, borderRadius: 10, background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <BulbOutlined style={{ color: '#fff', fontSize: 20 }} />
            </div>
            <div>
              <Title level={3} style={{ color: '#fff', marginBottom: 2 }}>Quản lý kỹ năng</Title>
              <Text style={{ color: 'rgba(255,255,255,0.8)' }}>{skills.length} kỹ năng trong hệ thống</Text>
            </div>
          </div>
        </div>
      </div>

      <div style={{ maxWidth: 800, margin: '-28px auto 0', padding: '0 16px 40px' }}>
        <div style={{ background: '#fff', borderRadius: 14, padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.07)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16, flexWrap: 'wrap', gap: 8 }}>
            <Title level={5} style={{ margin: 0 }}>Danh sách kỹ năng</Title>
            <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
              style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
              Thêm kỹ năng
            </Button>
          </div>
          <Table columns={columns} dataSource={skills} rowKey="id" loading={loading}
            pagination={{ pageSize: 20 }} />
        </div>
      </div>

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa kỹ năng' : 'Thêm kỹ năng mới'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="skillName" label={<span style={{ fontWeight: 600 }}>Tên kỹ năng</span>}
            rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input placeholder="Java, ReactJS, Python..." size="large" style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              Lưu
            </Button>
          </div>
        </Form>
      </Modal>
    </AppLayout>
  );
}
