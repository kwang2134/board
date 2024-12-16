interface HTMLElementEvent extends Event {
    target: HTMLElement;
}

document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.tab');
    const postTypeTitle = document.querySelector('.post-type-title');
    const searchInput = document.querySelector('.search-input') as HTMLInputElement;
    const searchDropdown = document.querySelector('.search-dropdown') as HTMLSelectElement;
    const postsFragment = document.getElementById('posts-fragment');
    const currentPostType = document.getElementById('currentPostType') as HTMLInputElement;
    const currentSearchType = document.getElementById('currentSearchType') as HTMLInputElement;
    const currentKeyword = document.getElementById('currentKeyword') as HTMLInputElement;
    const searchButton = document.getElementById('searchButton');

    // 탭 전환 이벤트
    tabs.forEach(tab => {
        tab.addEventListener('click', async () => {
            const type = tab.getAttribute('data-type');
            if (!type) return;

            currentSearchType.value = '';
            currentKeyword.value = '';
            searchInput.value = '';
            searchDropdown.value = 'TITLE';

            // 탭 활성화
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            currentPostType.value = type;

            // 타이틀 변경
            if (postTypeTitle) {
                postTypeTitle.textContent = `${type.charAt(0).toUpperCase() + type.slice(1).toLowerCase()} Post`;
            }

            try {
                const response = await fetch(`/posts/tab?type=${type}`, {
                    headers: {
                        'Accept': 'text/html',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const html = await response.text();
                if (postsFragment && html.trim()) {
                    postsFragment.innerHTML = html;
                }
            } catch (error) {
                console.error('Error loading posts:', error);
            }
        });
    });

    // 페이지네이션 이벤트 처리
    document.addEventListener('click', async (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target || !target.matches('.page-number, .page-button')) return;

        e.preventDefault();
        const link = target as HTMLAnchorElement;
        const url = new URL(link.href);

        // 검색 상태일 때는 URL 수정하지 않음 (이미 올바른 URL이 생성되어 있음)
        if (!currentSearchType.value || !currentKeyword.value) {
            url.searchParams.set('type', currentPostType.value);
        }

        try {
            const response = await fetch(url.toString(), {
                headers: {
                    'Accept': 'text/html',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const html = await response.text();
            if (postsFragment && html.trim()) {
                postsFragment.innerHTML = html;
            }
        } catch (error) {
            console.error('Error loading page:', error);
        }
    });

    // 검색 이벤트 처리
    const handleSearch = async () => {
        const keyword = searchInput?.value.trim();
        const searchType = searchDropdown?.value;

        if (!keyword) {
            alert('검색어를 입력해주세요.');
            return;
        }

        // 검색 상태 저장
        currentSearchType.value = searchType;
        currentKeyword.value = keyword;

        // 타이틀 변경
        if (postTypeTitle) {
            postTypeTitle.textContent = 'Search Post';
        }

        try {
            const response = await fetch(`/posts/search?searchType=${searchType}&keyword=${encodeURIComponent(keyword)}`, {
                headers: {
                    'Accept': 'text/html',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const html = await response.text();
            if (postsFragment && html.trim()) {
                postsFragment.innerHTML = html;
            }
        } catch (error) {
            console.error('Error searching posts:', error);
        }
    };

    searchButton?.addEventListener('click', () => {
        handleSearch();
    });

    // 검색어 입력 후 엔터 키 이벤트
    searchInput?.addEventListener('keypress', (e: KeyboardEvent) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSearch();
        }
    });
});
