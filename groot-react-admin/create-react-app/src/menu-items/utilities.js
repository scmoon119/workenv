// assets
import { IconTypography, IconPalette, IconShadow, IconWindmill } from '@tabler/icons';
// import PlaylistAddCheckIcon from '@mui/icons-material/PlaylistAddCheck'; // constant
// import AssignmentIndIcon from '@mui/icons-material/AssignmentInd';

const icons = {
  IconTypography,
  IconPalette,
  IconShadow,
  IconWindmill
};

// ==============================|| UTILITIES MENU ITEMS ||============================== //

const utilities = {
  id: 'utilities',
  title: 'Utilities',
  type: 'group',
  children: [
    {
      id: 'util-typography',
      title: 'Typography',
      type: 'item',
      url: '/utils/util-typography',
      icon: icons.IconTypography,
      breadcrumbs: false
    }
  ]
};

export default utilities;
