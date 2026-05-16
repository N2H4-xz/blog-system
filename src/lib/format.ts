import dayjs from 'dayjs';

export function formatDate(value?: string | null) {
  if (!value) {
    return '未发布';
  }
  return dayjs(value).format('YYYY-MM-DD HH:mm');
}

export function getErrorMessage(error: unknown) {
  const maybeMessage = (error as { response?: { data?: { message?: string } } })?.response?.data?.message;
  return maybeMessage ?? '请求失败，请稍后重试';
}
