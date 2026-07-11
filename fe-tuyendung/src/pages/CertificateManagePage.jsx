import React, { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, Space, message, Popconfirm, Typography
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { certificateApi } from '../api/services';
import AppLayout from '../components/AppLayout';

const { Title } = Typography;
const GREEN = '#00b14f';

export default function CertificateManagePage() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    certificateApi.getAll()
      .then((res) => { if (res.data.success) setList(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (item) => { setEditing(item); form.setFieldsValue(item); setModalOpen(true); };

  const onSave = async (values) => {
    setSaving(true);
    try {
      const res = editing
        ? await certificateApi.update({ ...values, id: editing.id })
        : await certificateApi.create(values);
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
      await certificateApi.delete(id);
      message.success('Đã xóa');
      load();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: 'Tên chứng chỉ', dataIndex: 'certificateName', key: 'certificateName' },
    { title: 'Loại điểm', dataIndex: 'scoreType', key: 'scoreType' },
    {
      title: 'Thao tác', key: 'actions', width: 100,
      render: (_, r) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(r)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa chứng chỉ này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 60px' }}>
        <div style={{ maxWidth: 900, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>Quản lý chứng chỉ</Title>
        </div>
      </div>

      <div style={{ maxWidth: 900, margin: '-32px auto 0', padding: '0 16px 32px' }}>
        <div style={{ background: '#fff', borderRadius: 14, padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.07)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <Title level={5} style={{ margin: 0 }}>Danh sách chứng chỉ</Title>
            <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
              style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
              Thêm chứng chỉ
            </Button>
          </div>
          <Table columns={columns} dataSource={list} rowKey="id" loading={loading}
            pagination={{ pageSize: 15, showSizeChanger: false }} />
        </div>
      </div>

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa chứng chỉ' : 'Thêm chứng chỉ'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null} width={460}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="certificateName" label={<span style={{ fontWeight: 600 }}>Tên chứng chỉ</span>}
            rules={[{ required: true, message: 'Nhập tên chứng chỉ' }]}>
            <Input placeholder="IELTS, TOEIC, AWS..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="scoreType" label={<span style={{ fontWeight: 600 }}>Loại điểm</span>}>
            <Input placeholder="Band, Score, Level..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              {editing ? 'Lưu thay đổi' : 'Thêm'}
            </Button>
          </div>
        </Form>
      </Modal>
    </AppLayout>
  );
}
