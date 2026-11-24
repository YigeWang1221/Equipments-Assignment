import request from '@/axios'

const downloadBlob = async (url: string): Promise<Blob> => {
  const res: any = await request.get({ url, responseType: 'blob' })
  return res.data as Blob
}

export const exportClassGrades = (classId: number | string): Promise<Blob> => {
  return downloadBlob(`/api/v1/exports/classes/${classId}/grades.xlsx`)
}

export const exportSubmissionPdf = (id: number | string): Promise<Blob> => {
  return downloadBlob(`/api/v1/exports/submissions/${id}.pdf`)
}

export const exportWorkPdfsZip = (workId: number | string): Promise<Blob> => {
  return downloadBlob(`/api/v1/exports/works/${workId}/pdfs.zip`)
}
