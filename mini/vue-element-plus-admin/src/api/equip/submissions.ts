import request from '@/axios'
import { unwrapData } from './types'
import type {
  ApiResponse,
  SubmissionDetailVO,
  SubmissionSaveRequest,
  SubmissionSummaryVO
} from './types'

export const getWorkSubmissions = async (
  workId: number | string
): Promise<SubmissionSummaryVO[]> => {
  const res = await request.get<ApiResponse<SubmissionSummaryVO[]>>({
    url: `/api/v1/works/${workId}/submissions`
  })
  return unwrapData<SubmissionSummaryVO[]>(res)
}

export const initMySubmission = async (workId: number | string): Promise<SubmissionDetailVO> => {
  const res = await request.post<ApiResponse<SubmissionDetailVO>>({
    url: `/api/v1/works/${workId}/submissions/init`
  })
  return unwrapData<SubmissionDetailVO>(res)
}

export const getMySubmission = async (workId: number | string): Promise<SubmissionDetailVO> => {
  const res = await request.get<ApiResponse<SubmissionDetailVO>>({
    url: `/api/v1/works/${workId}/my-submission`
  })
  return unwrapData<SubmissionDetailVO>(res)
}

export const getSubmission = async (id: number | string): Promise<SubmissionDetailVO> => {
  const res = await request.get<ApiResponse<SubmissionDetailVO>>({
    url: `/api/v1/submissions/${id}`
  })
  return unwrapData<SubmissionDetailVO>(res)
}

export const updateSubmission = async (
  id: number | string,
  data: SubmissionSaveRequest
): Promise<SubmissionDetailVO> => {
  const res = await request.put<ApiResponse<SubmissionDetailVO>>({
    url: `/api/v1/submissions/${id}`,
    data
  })
  return unwrapData<SubmissionDetailVO>(res)
}

export const scoreSubmission = async (
  id: number | string,
  score: number
): Promise<SubmissionDetailVO> => {
  const res = await request.post<ApiResponse<SubmissionDetailVO>>({
    url: `/api/v1/submissions/${id}/score`,
    data: { score }
  })
  return unwrapData<SubmissionDetailVO>(res)
}
