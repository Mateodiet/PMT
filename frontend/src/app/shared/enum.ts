export interface Project {
    projectName?: string,
    projectDescription?: string,
    creatorEmail?: string,
    projectStatus?: string
}
export interface users {
    id ?: string,
    name ?: string,
    email ?: string,
    password ?: string,
    role ?: string,
    contactNumber ?: string,
    isActive ?: boolean,
}

export interface task {
    taskName: string,
    taskDescription: string,
    taskStatus: string,
    projectName: string,
    creatorEmail: string
}