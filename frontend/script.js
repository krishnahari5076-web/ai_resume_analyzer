document.addEventListener('DOMContentLoaded', () => {
  const API_BASE_URL = window.AI_RESUME_API_URL || 'https://ai-resume-analyzer-1-r0nq.onrender.com';
  const MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;
  let selectedResumeFile = null;
  const $ = (id) => document.getElementById(id);
  const navToggle = $('navToggle'), navbarNav = $('navbarNav'), dropzone = $('dropzone');
  const fileInput = $('fileInput'), chooseFileBtn = $('chooseFileBtn'), removeFileBtn = $('removeFileBtn');
  const analyzeBtn = $('analyzeBtn'), uploadError = $('uploadError'), loading = $('loadingSection');
  $('year').textContent = new Date().getFullYear();

  navToggle?.addEventListener('click', () => { const open = navbarNav.classList.toggle('is-open'); navToggle.setAttribute('aria-expanded', String(open)); });
  document.querySelectorAll('.navbar__link').forEach(link => link.addEventListener('click', () => navbarNav.classList.remove('is-open')));
  chooseFileBtn?.addEventListener('click', () => fileInput.click());
  fileInput?.addEventListener('change', () => fileInput.files[0] && selectFile(fileInput.files[0]));
  removeFileBtn?.addEventListener('click', clearFile);
  ['dragenter', 'dragover'].forEach(name => dropzone?.addEventListener(name, e => { e.preventDefault(); dropzone.classList.add('is-dragover'); }));
  ['dragleave', 'drop'].forEach(name => dropzone?.addEventListener(name, e => { e.preventDefault(); dropzone.classList.remove('is-dragover'); }));
  dropzone?.addEventListener('drop', e => { const file = e.dataTransfer.files[0]; if (file) selectFile(file); });
  analyzeBtn?.addEventListener('click', analyze);

  function selectFile(file) {
    hideError();
    if (!(file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf'))) return showError('Only PDF resumes are supported.');
    if (file.size > MAX_FILE_SIZE_BYTES) return showError('File is too large. The limit is 10 MB.');
    selectedResumeFile = file; $('fileName').textContent = file.name; $('fileSize').textContent = formatSize(file.size); $('uploadFileInfo').hidden = false; analyzeBtn.disabled = false;
  }
  function clearFile() { selectedResumeFile = null; fileInput.value = ''; $('uploadFileInfo').hidden = true; analyzeBtn.disabled = true; }
  function showError(message) { uploadError.textContent = message; uploadError.hidden = false; }
  function hideError() { uploadError.hidden = true; uploadError.textContent = ''; }
  function formatSize(bytes) { return bytes < 1048576 ? `${(bytes / 1024).toFixed(1)} KB` : `${(bytes / 1048576).toFixed(2)} MB`; }
  function setLoading(visible) { loading.hidden = !visible; if (visible) { $('loadingProgressBar').style.width = '35%'; loading.scrollIntoView({ behavior: 'smooth', block: 'center' }); } }

  async function analyze() {
    if (!selectedResumeFile) return showError('Choose a PDF resume before analyzing.');
    hideError(); analyzeBtn.disabled = true; setLoading(true);
    try {
      const form = new FormData(); form.append('file', selectedResumeFile);
      const response = await fetch(`${API_BASE_URL}/api/resume/analyze`, { method: 'POST', body: form });
      const body = await response.json().catch(() => ({}));
      if (!response.ok) throw new Error(body.message || 'The analysis could not be completed.');
      $('loadingProgressBar').style.width = '100%'; renderResult(body);
    } catch (error) { showError(error.message || 'Unable to reach the analysis service. Please try again.'); }
    finally { setTimeout(() => setLoading(false), 350); analyzeBtn.disabled = !selectedResumeFile; }
  }
  function renderList(id, items) { const list = $(id); list.replaceChildren(...(items || []).map(item => { const li = document.createElement('li'); li.textContent = item; return li; })); }
  function renderResult(result) {
    $('resultFileName').textContent = result.fileName || selectedResumeFile.name; $('atsScore').textContent = `${result.atsScore ?? 0}/100`; $('resultSummary').textContent = result.summary || 'Your AI analysis is ready.';
    renderList('strengthsList', result.strengths); renderList('suggestionsList', result.suggestions); renderList('missingSkillsList', result.missingSkills);
    const tags = $('keywordsList'); tags.replaceChildren(...(result.keywords || []).map(keyword => { const tag = document.createElement('span'); tag.textContent = keyword; return tag; }));
    $('resultsSection').hidden = false; setTimeout(() => $('resultsSection').scrollIntoView({ behavior: 'smooth', block: 'start' }), 400);
  }
});
