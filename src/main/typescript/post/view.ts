document.addEventListener('DOMContentLoaded', () => {
    const commentsFragment = document.getElementById('comments-fragment');
    const postEditButton = document.querySelector('.edit-button') as HTMLElement;
    const postDeleteButton = document.querySelector('.post-delete') as HTMLElement;
    const userId = document.querySelector('#userId')?.getAttribute('value');

    // 게시글 수정 버튼 클릭 이벤트
    postEditButton?.addEventListener('click', async () => {
        const postId = window.location.pathname.split('/').pop();

        if (!userId) {
            const password = prompt('비밀번호를 입력하세요:');
            if (!password) return;

            try {
                const response = await fetch(`/post/${postId}/edit?password=${encodeURIComponent(password)}`, {
                    headers: {
                        'Accept': 'application/json, text/html',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (!response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    return;
                }

                window.location.href = `/post/${postId}/edit?password=${encodeURIComponent(password)}`;
            } catch (error) {
                console.error('Error checking password:', error);
                alert('처리 중 오류가 발생했습니다.');
            }
        } else {
            window.location.href = `/post/${postId}/edit`;
        }
    });

    // 게시글 삭제 버튼 클릭 이벤트
    postDeleteButton?.addEventListener('click', async () => {
        const postId = window.location.pathname.split('/').pop();

        if (!userId) {
            const password = prompt('비밀번호를 입력하세요:');
            if (!password) return;

            try {
                const response = await fetch(`/post/${postId}/delete?password=${encodeURIComponent(password)}`, {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (!response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    return;
                }

                window.location.href = '/';
            } catch (error) {
                console.error('Error deleting post:', error);
                alert('처리 중 오류가 발생했습니다.');
            }
        } else {
            try {
                const response = await fetch(`/post/${postId}/delete`, {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (!response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    return;
                }

                window.location.href = '/';
            } catch (error) {
                console.error('Error deleting post:', error);
                alert('처리 중 오류가 발생했습니다.');
            }
        }
    });

    // 페이지네이션 이벤트 처리
    document.addEventListener('click', async (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target || !target.matches('.page-number, .page-button')) return;

        e.preventDefault();
        const link = target as HTMLAnchorElement;

        try {
            const response = await fetch(link.href, {
                headers: {
                    'Accept': 'text/html',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) throw new Error('Network response was not ok');

            const html = await response.text();
            if (commentsFragment && html.trim()) {
                commentsFragment.innerHTML = html;
            }
        } catch (error) {
            console.error('Error loading comments:', error);
        }
    });

    // 댓글 수정 버튼 클릭 이벤트
    document.addEventListener('click', async (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target.matches('.comment-edit')) return;

        const comment = target.closest('.comment') as HTMLElement;
        const editContainer = comment?.querySelector('.edit-form-container') as HTMLElement;
        if (!comment || !editContainer) return;

        const postId = window.location.pathname.split('/').pop();
        const commentId = comment.dataset.commentId;
        const commentUserId = comment.dataset.userId;

        try {
            if (!commentUserId) {
                const password = prompt('비밀번호를 입력하세요:');
                if (!password) return;

                const response = await fetch(`/post/${postId}/comment/${commentId}/edit?password=${encodeURIComponent(password)}`, {
                    headers: {
                        'Accept': 'application/json, text/html',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (!response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    return;
                }

                const html = await response.text();
                editContainer.innerHTML = html;
                editContainer.style.display = 'block';
            } else {
                const response = await fetch(`/post/${postId}/comment/${commentId}/edit`, {
                    headers: {
                        'Accept': 'application/json, text/html',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (!response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    return;
                }

                const html = await response.text();
                editContainer.innerHTML = html;
                editContainer.style.display = 'block';
            }
        } catch (error) {
            console.error('Error loading edit form:', error);
            alert('처리 중 오류가 발생했습니다.');
        }
    });

    // 댓글 삭제 버튼 클릭 이벤트
    document.addEventListener('click', async (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target.matches('.comment-delete')) return;

        const comment = target.closest('.comment') as HTMLElement;
        if (!comment) return;

        const postId = window.location.pathname.split('/').pop();
        const commentId = comment.dataset.commentId;
        const commentUserId = comment.dataset.userId;

        if (!commentUserId) {
            const password = prompt('비밀번호를 입력하세요:');
            if (!password) return;

            try {
                const response = await fetch(`/post/${postId}/comment/${commentId}/delete?password=${encodeURIComponent(password)}`, {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (!response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    return;
                }

                window.location.reload();
            } catch (error) {
                console.error('Error deleting comment:', error);
                alert('처리 중 오류가 발생했습니다.');
            }
        } else {
            try {
                const response = await fetch(`/post/${postId}/comment/${commentId}/delete`, {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (!response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    return;
                }

                window.location.reload();
            } catch (error) {
                console.error('Error deleting comment:', error);
                alert('처리 중 오류가 발생했습니다.');
            }
        }
    });

    // 추천/비추천 버튼 이벤트
    document.addEventListener('click', async (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target.matches('.recommend, .unrecommend')) return;

        const isRecommend = target.classList.contains('recommend');
        const postId = window.location.pathname.split('/').pop();

        try {
            const response = await fetch(`/api/post/${postId}/${isRecommend ? 'recommend' : 'not-recommend'}`, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            const result = await response.json();

            if (!response.ok) {
                alert(result.message);
                return;
            }

            const countElement = target.querySelector('span');
            if (countElement) {
                countElement.textContent = isRecommend ? result.recomCount : result.notRecomCount;
            }
            alert(result.message);
        } catch (error) {
            console.error('Error processing recommendation:', error);
            alert('처리 중 오류가 발생했습니다.');
        }
    });

    // 댓글 수정 취소 버튼 이벤트
    document.addEventListener('click', (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target.matches('.cancel-edit')) return;

        const editContainer = target.closest('.edit-form-container') as HTMLElement;
        if (editContainer) {
            editContainer.style.display = 'none';
            editContainer.innerHTML = '';
        }
    });

    // 댓글 내용 클릭 시 대댓글 작성 모드
    document.addEventListener('click', (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target.matches('.comment-text')) return;

        const comment = target.closest('.comment') as HTMLElement;
        if (!comment) return;

        // 대댓글인 경우 클릭 무시
        if (comment.closest('.reply')) return;

        const commentId = comment.dataset.commentId;
        const commentText = target.textContent || '';

        // 댓글 내용 축약 (8자 + ...)
        const truncatedText = commentText.length > 8
            ? commentText.substring(0, 8) + '...'
            : commentText;

        // 대댓글 작성 모드 활성화
        const replyInfo = document.getElementById('reply-info');
        const replyToText = replyInfo?.querySelector('.reply-to');
        const parentCommentInput = document.getElementById('parentCommentId') as HTMLInputElement;

        if (replyInfo && replyToText && parentCommentInput) {
            replyInfo.style.display = 'block';
            replyToText.textContent = `"${truncatedText}" 에 대한 댓글 작성 중`;
            replyToText.className = 'reply-text';
            parentCommentInput.value = commentId || '';
        }
    });

    // 대댓글 작성 취소
    document.addEventListener('click', (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target.matches('.cancel-reply')) return;

        const replyInfo = document.getElementById('reply-info');
        const parentCommentInput = document.getElementById('parentCommentId') as HTMLInputElement;

        if (replyInfo && parentCommentInput) {
            replyInfo.style.display = 'none';
            parentCommentInput.value = '';
        }
    });
});
