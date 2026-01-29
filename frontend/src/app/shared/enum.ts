export interface Project {
    projectId?: number;
    projectName?: string;
    projectDescription?: string;
    projectStartDate?: string;
    projectCreatedDate?: string;
    creatorEmail?: string;
    projectStatus?: string;
}

export interface users {
    userId?: number;
    name?: string;
    email?: string;
    password?: string;
    role?: string;
    contactNumber?: string;
    isActive?: boolean;
}

export interface task {
    taskId?: number;
    taskName: string;
    taskDescription: string;
    taskStatus: string;
    priority?: string;
    taskDueDate?: string;
    taskCreatedAt?: string;
    taskCompletedAt?: string;
    projectName: string;
    creatorEmail: string;
}

export interface ProjectMember {
    userId: number;
    email: string;
    name: string;
    role: string;
    status: string;
    joinedAt: string;
}

export interface TaskHistory {
    historyId: number;
    fieldName: string;
    oldValue: string;
    newValue: string;
    changeDescription: string;
    modifiedByEmail: string;
    modifiedAt: string;
}