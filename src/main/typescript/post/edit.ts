document.addEventListener('DOMContentLoaded', () => {
    const imageUpload = document.getElementById('imageUpload') as HTMLInputElement;
    const editForm = document.getElementById('editForm') as HTMLFormElement;
    const textarea = document.querySelector('.edit-textarea') as HTMLTextAreaElement;

    // 이미지 미리보기 컨테이너 생성
    const previewContainer = document.createElement('div');
    previewContainer.className = 'image-preview-container';
    previewContainer.style.marginBottom = '20px';
    textarea.parentNode?.insertBefore(previewContainer, textarea);

    // 이미지 업로드 처리
    imageUpload?.addEventListener('change', async (e: Event) => {
        const target = e.target as HTMLInputElement;
        const file = target.files?.[0];

        if (!file) return;

        // 파일 크기 체크 (4MB)
        if (file.size > 4 * 1024 * 1024) {
            alert('이미지의 크기가 너무 큽니다. (최대 4MB)');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch('/api/photos/temp-upload', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error('Upload failed');
            }

            const imagePath = await response.text();

            // 이미지 미리보기 생성
            const previewImg = document.createElement('img');
            previewImg.src = imagePath;
            previewImg.style.maxWidth = '300px';
            previewImg.style.marginRight = '10px';
            previewImg.style.marginBottom = '10px';
            previewContainer.appendChild(previewImg);

            // 텍스트 영역에 마크다운 이미지 문법 추가
            if (textarea) {
                const cursorPos = textarea.selectionStart;
                const textBefore = textarea.value.substring(0, cursorPos);
                const textAfter = textarea.value.substring(cursorPos);
                textarea.value = textBefore + `![image](${imagePath})` + textAfter;
            }
        } catch (error) {
            console.error('Error uploading image:', error);
            alert('이미지 업로드에 실패했습니다.');
        }
    });

    // 기존 이미지 미리보기 표시
    const content = textarea.value;
    const imgRegex = /!\[image\]\((.*?)\)/g;
    let match;

    while ((match = imgRegex.exec(content)) !== null) {
        const imagePath = match[1];
        const previewImg = document.createElement('img');
        previewImg.src = imagePath;
        previewImg.style.maxWidth = '300px';
        previewImg.style.marginRight = '10px';
        previewImg.style.marginBottom = '10px';
        previewContainer.appendChild(previewImg);
    }
});
