export function normalizeFilterText(value) {
  if (value === null || value === undefined) {
    return ''
  }
  return String(value).trim().toLowerCase()
}

export function matchesKeyword(row, keyword, fields = []) {
  const needle = normalizeFilterText(keyword)
  if (!needle) {
    return true
  }

  return fields.some((field) => normalizeFilterText(row?.[field]).includes(needle))
}

export function filterRows(rows = [], { keyword = '', fields = [], predicates = [] } = {}) {
  return rows.filter((row) => {
    if (!matchesKeyword(row, keyword, fields)) {
      return false
    }
    return predicates.every((predicate) => predicate(row))
  })
}

export function uniqueValues(rows = [], field) {
  return Array.from(
    new Set(
      rows
        .map((row) => row?.[field])
        .filter((value) => value !== null && value !== undefined && value !== '')
    )
  )
}

export function pickPreferredValue(values = [], preferred = '') {
  if (preferred && values.includes(preferred)) {
    return preferred
  }
  return values[0] || ''
}
