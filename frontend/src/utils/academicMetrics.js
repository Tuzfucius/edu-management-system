export const GRADE_BUCKETS = ['90-100', '80-89', '70-79', '60-69', '<60']

export const LEVEL_COLORS = {
  优秀: '#16a34a',
  良好: '#2563eb',
  及格: '#f59e0b',
  不及格: '#dc2626'
}

export const BUCKET_COLORS = ['#16a34a', '#2563eb', '#7c3aed', '#f59e0b', '#dc2626']

export const COURSE_COLORS = ['#2563eb', '#16a34a', '#f59e0b', '#7c3aed', '#dc2626', '#0891b2', '#db2777', '#4f46e5']

export function getCurrentSemester(date = new Date()) {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  if (month >= 9) {
    return `${year}-${year + 1}-1`
  }
  if (month === 1) {
    return `${year - 1}-${year}-1`
  }
  return `${year - 1}-${year}-2`
}

export function scoreLevel(score) {
  const value = Number(score)
  if (value >= 90) return '优秀'
  if (value >= 80) return '良好'
  if (value >= 60) return '及格'
  return '不及格'
}

export function buildScoreBuckets(rows) {
  const buckets = Object.fromEntries(GRADE_BUCKETS.map((item) => [item, 0]))
  rows.forEach((row) => {
    const score = Number(row.score)
    if (score >= 90) buckets['90-100'] += 1
    else if (score >= 80) buckets['80-89'] += 1
    else if (score >= 70) buckets['70-79'] += 1
    else if (score >= 60) buckets['60-69'] += 1
    else buckets['<60'] += 1
  })
  return GRADE_BUCKETS.map((name) => ({ name, value: buckets[name] }))
}

export function buildLevelRows(rows) {
  const levels = { 优秀: 0, 良好: 0, 及格: 0, 不及格: 0 }
  rows.forEach((row) => {
    levels[scoreLevel(row.score)] += 1
  })
  return Object.entries(levels).map(([name, value]) => ({
    name,
    value,
    itemStyle: { color: LEVEL_COLORS[name] }
  }))
}

export function averageScore(rows) {
  if (!rows.length) return '-'
  return (rows.reduce((sum, row) => sum + Number(row.score), 0) / rows.length).toFixed(1)
}

export function medianScore(rows) {
  if (!rows.length) return '-'
  const values = rows.map((row) => Number(row.score)).sort((a, b) => a - b)
  const middle = Math.floor(values.length / 2)
  return values.length % 2 ? values[middle].toFixed(1) : ((values[middle - 1] + values[middle]) / 2).toFixed(1)
}

export function fivePointGpa(rows) {
  const graded = rows.filter((row) => Number(row.gradeStatus) === 1 && row.score !== null && row.score !== undefined && Number(row.credit || 0) > 0)
  const credits = graded.reduce((sum, row) => sum + Number(row.credit || 0), 0)
  if (!credits) return '-'
  const points = graded.reduce((sum, row) => sum + scoreToPoint(row.score) * Number(row.credit || 0), 0)
  return (points / credits).toFixed(2)
}

export function scoreToPoint(score) {
  const value = Number(score)
  if (value >= 90) return 5
  if (value >= 80) return 4
  if (value >= 70) return 3
  if (value >= 60) return 2
  return 0
}

export function courseColor(key) {
  const text = String(key ?? '')
  let hash = 0
  for (let index = 0; index < text.length; index += 1) {
    hash = (hash * 31 + text.charCodeAt(index)) >>> 0
  }
  return COURSE_COLORS[hash % COURSE_COLORS.length]
}
