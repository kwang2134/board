document.addEventListener('DOMContentLoaded', () => {
    // 탭 전환
    const tabs = document.querySelectorAll('.tab-button');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetId = (tab as HTMLElement).dataset.tab;

            // 탭 활성화
            document.querySelectorAll('.tab-button').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // 컨테이너 전환
            document.querySelectorAll('.user-list-container').forEach(c => c.classList.remove('active'));
            document.getElementById(`${targetId}-users`)?.classList.add('active');
        });
    });

    // Appoint 버튼 이벤트
    document.querySelectorAll('.appoint-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const userId = (e.target as HTMLElement).dataset.id;
            if (confirm('해당 사용자를 매니저로 승급하시겠습니까?')) {
                try {
                    const response = await fetch(`/api/admin/manage/appoint?id=${userId}`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    });
                    if (response.ok) {
                        alert('승급이 완료되었습니다.');
                        window.location.reload();
                    }
                } catch (error) {
                    alert('처리 중 오류가 발생했습니다.');
                }
            }
        });
    });

    // Ban 버튼 이벤트
    document.querySelectorAll('.ban-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const userId = (e.target as HTMLElement).dataset.id;
            if (confirm('해당 사용자를 정말 차단하시겠습니까?')) {
                try {
                    const response = await fetch(`/api/admin/manage/ban?id=${userId}`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    });
                    if (response.ok) {
                        alert('차단이 완료되었습니다.');
                        window.location.reload();
                    }
                } catch (error) {
                    alert('처리 중 오류가 발생했습니다.');
                }
            }
        });
    });

    // Demote 버튼 이벤트
    document.querySelectorAll('.demote-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const userId = (e.target as HTMLElement).dataset.id;
            if (confirm('해당 매니저를 일반 사용자로 강등하시겠습니까?')) {
                try {
                    const response = await fetch(`/api/admin/manage/demote?id=${userId}`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    });
                    if (response.ok) {
                        alert('강등이 완료되었습니다.');
                        window.location.reload();
                    }
                } catch (error) {
                    alert('처리 중 오류가 발생했습니다.');
                }
            }
        });
    });
});
