import {GuideDto} from "@/app/guides/services/guideService";


// formData를 GuideDto로 변환하는 함수
export const convertToGuideDto = (formData: any): Partial<GuideDto> => {
    const dto: Partial<GuideDto> = {
        name: formData.name || "",
        languages: formData.languages || "",
        activityRegion: formData.activityRegion || "",
        experienceYears: formData.experienceYears || 0,
        introduction: formData.introduction || "",
    };
    return dto;
}

// 백엔드에서 전달받은 GuideDto 를 guideData로 변환
export function convertFromGuideDto(guide: GuideDto): any {
    return {
        ...guide,
    };
}
