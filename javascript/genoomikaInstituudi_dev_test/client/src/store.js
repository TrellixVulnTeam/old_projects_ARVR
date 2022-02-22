import { computed, inject, provide, reactive } from 'vue'


// state management
export const initStore = () => {

    const store = reactive({
        upload: {
            state: 'idle',
            loading: false,
            message: null,
            error: false,
            selectedFiles: [],
            uploads: []
        },
        search: {
            state: 'idle',
            loading: false,
            message: null,
            error: false,
            result: [],
            statement: '',
            currentSort: '#',
            currentSortDir: 'asc',
            currentDisplay: 1
        }
    })

    // upload
    const getUploadState = computed(() => store.upload.state)
    const getUploadLoading = computed(() => store.upload.loading)
    const getUploadMessage = computed(() => store.upload.message)
    const getUploadError = computed(() => store.upload.error)
    const getUploadSelectedFiles = computed(() => store.upload.selectedFiles)
    const getUploadUploads = computed(() => store.upload.uploads)
    
    // search
    const getSearchState = computed(() => store.search.state)
    const getSearchLoading = computed(() => store.search.loading)
    const getSearchMessage = computed(() => store.search.message)
    const getSearchError = computed(() => store.search.error)
    const getSearchResult = computed(() => store.search.result)
    const getSearchStatement = computed(() => store.search.statement)
    const getSearchCurrentSort = computed(() => store.search.currentSort)
    const getSearchCurrentSortDir = computed(() => store.search.currentSortDir)
    const getSearchCurrentDisplay = computed(() => store.search.currentDisplay)

    // upload
    const updateUploadState = (state) => { store.upload.state = state }
    const updateUploadLoading = (loading) => { store.upload.loading = loading }
    const updateUploadMessage = (message) => { store.upload.message = message }
    const updateUploadError = (error) => { store.upload.error = error }
    const updateUploadSelectedFiles = (selectedFiles) => { store.upload.selectedFiles = selectedFiles }
    const updateUploadUploads = (uploads) => { store.upload.uploads = uploads }
    
    // search
    const updateSearchState = (state) => { store.search.state = state }
    const updateSearchLoading = (loading) => { store.search.loading = loading }
    const updateSearchMessage = (message) => { store.search.message = message }
    const updateSearchError = (error) => { store.search.error = error }
    const updateSearchResult = (result) => { store.search.result = result }
    const updateSearchStatement = (statement) => { store.search.statement = statement }
    const updateSearchCurrentSort = (currentSort) => { store.search.currentSort = currentSort }
    const updateSearchCurrentSortDir = (currentSortDir) => { store.search.currentSortDir = currentSortDir }
    const updateSearchCurrentDisplay = (currentDisplay) => { store.search.currentDisplay = currentDisplay }


    provide('getUploadState', getUploadState)
    provide('getUploadLoading', getUploadLoading)
    provide('getUploadMessage', getUploadMessage)
    provide('getUploadError', getUploadError)
    provide('getUploadSelectedFiles', getUploadSelectedFiles)
    provide('getUploadUploads', getUploadUploads)
    provide('getSearchState', getSearchState)
    provide('getSearchLoading', getSearchLoading)
    provide('getSearchMessage', getSearchMessage)
    provide('getSearchError', getSearchError)
    provide('getSearchResult', getSearchResult)
    provide('getSearchStatement', getSearchStatement)
    provide('getSearchCurrentSort', getSearchCurrentSort)
    provide('getSearchCurrentSortDir', getSearchCurrentSortDir)
    provide('getSearchCurrentDisplay', getSearchCurrentDisplay)
    provide('updateUploadState', updateUploadState)
    provide('updateUploadLoading', updateUploadLoading)
    provide('updateUploadMessage', updateUploadMessage)
    provide('updateUploadError', updateUploadError)
    provide('updateUploadSelectedFiles', updateUploadSelectedFiles)
    provide('updateUploadUploads', updateUploadUploads)
    provide('updateSearchState', updateSearchState)
    provide('updateSearchLoading', updateSearchLoading)
    provide('updateSearchMessage', updateSearchMessage)
    provide('updateSearchError', updateSearchError)
    provide('updateSearchResult', updateSearchResult)
    provide('updateSearchStatement', updateSearchStatement)
    provide('updateSearchCurrentSort', updateSearchCurrentSort)
    provide('updateSearchCurrentSortDir', updateSearchCurrentSortDir)
    provide('updateSearchCurrentDisplay', updateSearchCurrentDisplay)

}

export const useStore = () => ({
    getUploadState: inject('getUploadState'),
    getUploadLoading: inject('getUploadLoading'),
    getUploadMessage: inject('getUploadMessage'),
    getUploadError: inject('getUploadError'),
    getUploadSelectedFiles: inject('getUploadSelectedFiles'),
    getUploadUploads: inject('getUploadUploads'),
    getSearchState: inject('getSearchState'),
    getSearchLoading: inject('getSearchLoading'),
    getSearchMessage: inject('getSearchMessage'),
    getSearchError: inject('getSearchError'),
    getSearchResult: inject('getSearchResult'),
    getSearchStatement: inject('getSearchStatement'),
    getSearchCurrentSort: inject('getSearchCurrentSort'),
    getSearchCurrentSortDir: inject('getSearchCurrentSortDir'),
    getSearchCurrentDisplay: inject('getSearchCurrentDisplay'),
    updateUploadState: inject('updateUploadState'),
    updateUploadLoading: inject('updateUploadLoading'),
    updateUploadMessage: inject('updateUploadMessage'),
    updateUploadError: inject('updateUploadError'),
    updateUploadSelectedFiles: inject('updateUploadSelectedFiles'),
    updateUploadUploads: inject('updateUploadUploads'),
    updateSearchState: inject('updateSearchState'),
    updateSearchLoading: inject('updateSearchLoading'),
    updateSearchMessage: inject('updateSearchMessage'),
    updateSearchError: inject('updateSearchError'),
    updateSearchResult: inject('updateSearchResult'),
    updateSearchStatement: inject('updateSearchStatement'),
    updateSearchCurrentSort: inject('updateSearchCurrentSort'),
    updateSearchCurrentSortDir: inject('updateSearchCurrentSortDir'),
    updateSearchCurrentDisplay: inject('updateSearchCurrentDisplay')
})