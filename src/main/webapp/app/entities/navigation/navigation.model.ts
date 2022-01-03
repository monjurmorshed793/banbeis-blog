export interface INavigation {
  id?: string;
  sequence?: number | null;
  route?: string;
  title?: string;
  breadCrumb?: string | null;
  parent?: INavigation | null;
}

export class Navigation implements INavigation {
  constructor(
    public id?: string,
    public sequence?: number | null,
    public route?: string,
    public title?: string,
    public breadCrumb?: string | null,
    public parent?: INavigation | null
  ) {}
}

export function getNavigationIdentifier(navigation: INavigation): string | undefined {
  return navigation.id;
}
