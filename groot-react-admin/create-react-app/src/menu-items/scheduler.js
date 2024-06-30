// assets
// import { IconKey } from '@tabler/icons';
import PlaylistAddCheckIcon from '@mui/icons-material/PlaylistAddCheck';
import AssignmentIndIcon from '@mui/icons-material/AssignmentInd';

// constant
// const icons = {
//   IconKey
// };

// ==============================|| EXTRA PAGES MENU ITEMS ||============================== //

const schedulers = {
  id: 'schedulers',
  title: '업무관리',
  type: 'group',
  children: [
    {
      id: 'todo-list',
      title: 'Task 관리',
      type: 'item',
      icon: PlaylistAddCheckIcon,
      url: '/scheduler',
      breadcrumbs: false
    },
    {
      id: 'icons',
      title: '퍼포먼스 관리',
      type: 'collapse',
      icon: AssignmentIndIcon,
      children: [
        {
          id: 'perforamnce-log1',
          title: '퍼포먼스 로그 입력',
          type: 'item',
          url: '/performance-log1',
          breadcrumbs: false
        },
        {
          id: 'follower-management',
          title: '구성원 관리',
          type: 'item',
          url: '/follower-management',
          breadcrumbs: false
        },
        {
          id: 'perforamnce-log-search',
          title: '퍼포먼스 로그 조회',
          type: 'item',
          url: '/performance-log-search',
          breadcrumbs: false
        }
      ]
    }
  ]
};

export default schedulers;
