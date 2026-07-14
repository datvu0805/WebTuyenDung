"""
AI service gợi ý việc làm cho WebTuyenDung.

Thuật toán content-based đơn giản, không cần train model nặng vì lượng dữ liệu tương tác
(favorite/apply) hiện tại còn rất ít:

  1. skill_score   — Jaccard similarity giữa kỹ năng ứng viên và kỹ năng job yêu cầu.
  2. content_score — TF-IDF + cosine similarity giữa kỹ năng ứng viên (ghép thành câu) và
                      nội dung job (title + description). Đây là kỹ thuật machine learning
                      kinh điển (vector hóa văn bản), chạy nhẹ trên CPU.
  3. salary_score  — tỉ lệ chồng lấp giữa khoảng lương mong muốn của ứng viên và khoảng
                      lương của job.

final_score = 0.45*skill_score + 0.35*content_score + 0.20*salary_score
"""

from typing import List, Optional

from fastapi import FastAPI
from pydantic import BaseModel
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = FastAPI(title="WebTuyenDung AI Recommendation Service")

SKILL_WEIGHT = 0.45
CONTENT_WEIGHT = 0.35
SALARY_WEIGHT = 0.20


class JobInput(BaseModel):
    id: int
    title: str = ""
    description: str = ""
    skills: List[str] = []
    minSalary: Optional[float] = None
    maxSalary: Optional[float] = None


class RecommendRequest(BaseModel):
    candidateSkills: List[str] = []
    desiredMinSalary: Optional[float] = None
    desiredMaxSalary: Optional[float] = None
    jobs: List[JobInput] = []


class JobScore(BaseModel):
    jobId: int
    score: float


def jaccard_similarity(a: set, b: set) -> float:
    if not a and not b:
        return 0.0
    union = a | b
    if not union:
        return 0.0
    return len(a & b) / len(union)


def salary_overlap_score(desired_min, desired_max, job_min, job_max) -> float:
    if desired_min is None and desired_max is None:
        return 0.0
    if job_min is None and job_max is None:
        return 0.0

    d_min = desired_min if desired_min is not None else 0
    d_max = desired_max if desired_max is not None else d_min
    j_min = job_min if job_min is not None else 0
    j_max = job_max if job_max is not None else j_min

    overlap_start = max(d_min, j_min)
    overlap_end = min(d_max, j_max)

    if overlap_end <= overlap_start:
        return 0.0

    overlap = overlap_end - overlap_start
    desired_range = max(d_max - d_min, 1.0)

    return min(overlap / desired_range, 1.0)


def compute_content_scores(candidate_skills: List[str], jobs: List[JobInput]) -> List[float]:
    """TF-IDF cosine similarity giữa text kỹ năng ứng viên và text (title+description) của mỗi job."""

    if not candidate_skills or not jobs:
        return [0.0] * len(jobs)

    candidate_text = " ".join(candidate_skills)
    job_texts = [f"{job.title} {job.description}".strip() for job in jobs]

    corpus = [candidate_text] + job_texts

    # Nếu toàn bộ corpus rỗng (job không có title/description) thì TF-IDF không có gì để học
    if not any(text.strip() for text in corpus):
        return [0.0] * len(jobs)

    try:
        vectorizer = TfidfVectorizer(stop_words=None)
        tfidf_matrix = vectorizer.fit_transform(corpus)
    except ValueError:
        # corpus chỉ chứa stop words hoặc rỗng sau khi xử lý — không tính được vector
        return [0.0] * len(jobs)

    candidate_vector = tfidf_matrix[0:1]
    job_vectors = tfidf_matrix[1:]

    similarities = cosine_similarity(candidate_vector, job_vectors)[0]

    return [float(s) for s in similarities]


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/recommend", response_model=List[JobScore])
def recommend(req: RecommendRequest):

    candidate_skill_set = {s.strip().lower() for s in req.candidateSkills if s.strip()}
    content_scores = compute_content_scores(req.candidateSkills, req.jobs)

    results: List[JobScore] = []

    for job, content_score in zip(req.jobs, content_scores):

        job_skill_set = {s.strip().lower() for s in job.skills if s.strip()}

        skill_score = jaccard_similarity(candidate_skill_set, job_skill_set)
        sal_score = salary_overlap_score(
            req.desiredMinSalary, req.desiredMaxSalary, job.minSalary, job.maxSalary
        )

        final_score = (
            SKILL_WEIGHT * skill_score
            + CONTENT_WEIGHT * content_score
            + SALARY_WEIGHT * sal_score
        )

        results.append(JobScore(jobId=job.id, score=round(final_score, 6)))

    results.sort(key=lambda x: x.score, reverse=True)

    return results
